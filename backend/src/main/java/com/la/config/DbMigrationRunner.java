package com.la.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 轻量数据库迁移执行器（替代 Flyway：社区版已不支持 MySQL 5.7）。
 * 约定：classpath:db/migration/V{version}__{desc}.sql，按版本号升序执行，
 * 已应用版本记录在 schema_version 表。语句以独占一行的分号结尾切分。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DbMigrationRunner implements InitializingBean {

    private static final Pattern VERSION_PATTERN = Pattern.compile("^V(\\d+)__.*\\.sql$");

    private final DataSource dataSource;

    /**
     * 在单例初始化阶段执行迁移（早于 Tomcat 开始接收请求、早于任何业务 SQL）
     */
    @Override
    public void afterPropertiesSet() {
        migrate();
    }

    public void migrate() {
        try (Connection conn = dataSource.getConnection()) {
            ensureVersionTable(conn);
            List<String> applied = appliedVersions(conn);
            List<Resource> scripts = sortedScripts();
            for (Resource res : scripts) {
                Matcher m = VERSION_PATTERN.matcher(res.getFilename());
                if (!m.matches()) {
                    continue;
                }
                String version = m.group(1);
                if (applied.contains(version)) {
                    continue;
                }
                log.info("应用数据库迁移: {} ({})", version, res.getFilename());
                for (String sql : splitStatements(readScript(res))) {
                    try (Statement st = conn.createStatement()) {
                        st.execute(sql);
                    }
                }
                try (Statement st = conn.createStatement()) {
                    st.execute("INSERT INTO schema_version (version, script, applied_at) VALUES ('"
                            + version + "', '" + res.getFilename() + "', NOW())");
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("数据库迁移失败: " + e.getMessage(), e);
        }
    }

    private void ensureVersionTable(Connection conn) throws Exception {
        try (Statement st = conn.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS schema_version (" +
                    "version VARCHAR(20) NOT NULL PRIMARY KEY," +
                    "script VARCHAR(200) NOT NULL," +
                    "applied_at DATETIME NOT NULL)");
        }
    }

    private List<String> appliedVersions(Connection conn) throws Exception {
        List<String> list = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT version FROM schema_version")) {
            while (rs.next()) {
                list.add(rs.getString(1));
            }
        }
        return list;
    }

    private List<Resource> sortedScripts() throws Exception {
        Resource[] resources = new PathMatchingResourcePatternResolver()
                .getResources("classpath:db/migration/V*.sql");
        List<Resource> list = new ArrayList<>(List.of(resources));
        list.sort((a, b) -> versionOf(a.getFilename()).compareTo(versionOf(b.getFilename())));
        return list;
    }

    private String versionOf(String filename) {
        Matcher m = VERSION_PATTERN.matcher(filename);
        return m.matches() ? String.format("%06d", Integer.parseInt(m.group(1))) : "zzz";
    }

    private String readScript(Resource res) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(res.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        }
        return sb.toString();
    }

    /**
     * 引号感知的语句切分：仅在单引号字符串外遇到 ";" 时切分；
     * 支持 \' 与 '' 转义、-- 与 # 行注释。字符串内的真实换行与分号均安全。
     */
    private List<String> splitStatements(String script) {
        List<String> statements = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inString = false;
        int n = script.length();
        for (int i = 0; i < n; i++) {
            char c = script.charAt(i);
            if (inString) {
                current.append(c);
                if (c == '\\') {
                    if (i + 1 < n) {
                        current.append(script.charAt(++i));
                    }
                } else if (c == '\'') {
                    if (i + 1 < n && script.charAt(i + 1) == '\'') {
                        current.append(script.charAt(++i));
                    } else {
                        inString = false;
                    }
                }
                continue;
            }
            if (c == '\'') {
                inString = true;
                current.append(c);
            } else if ((c == '-' && i + 1 < n && script.charAt(i + 1) == '-') || c == '#') {
                while (i < n && script.charAt(i) != '\n') {
                    i++;
                }
                current.append('\n');
            } else if (c == ';') {
                String sql = current.toString().trim();
                if (!sql.isEmpty()) {
                    statements.add(sql);
                }
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        String rest = current.toString().trim();
        if (!rest.isEmpty()) {
            statements.add(rest);
        }
        return statements;
    }
}
