package ru.javawebinar.basejava.storage;

import ru.javawebinar.basejava.exception.NotExistStorageException;
import ru.javawebinar.basejava.model.*;
import ru.javawebinar.basejava.sql.SqlHelper;

import java.sql.*;
import java.util.*;

// TODO implement Section (except OrganizationSection)
// TODO Join and split ListSection by `\n`
public class SqlStorage implements Storage {
    public final SqlHelper sqlHelper;

    public SqlStorage(String dbUrl, String dbUser, String dbPassword) {
        sqlHelper = new SqlHelper(() -> DriverManager.getConnection(dbUrl, dbUser, dbPassword));
    }

    @Override
    public void clear() {
        sqlHelper.execute("DELETE FROM resume");
    }

    @Override
    public Resume get(String uuid) {
        return sqlHelper.execute("" +
                        "    SELECT * FROM resume r " +
                        " LEFT JOIN contact c " +
                        "        ON r.uuid = c.resume_uuid " +
                        " LEFT JOIN section s" +
                        "        ON r.uuid = s.resume_uuid" +
                        "     WHERE r.uuid =? ",

            ps -> {
                ps.setString(1, uuid);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    throw new NotExistStorageException(uuid);
                }
                Resume r = new Resume(uuid, rs.getString("full_name"));
                do {
                    addContact(rs, r);
                    addSection(rs, r);
                } while (rs.next());

                return r;
        });
    }

    @Override
    public void update(Resume r) {
        sqlHelper.transactionalExecute(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("UPDATE resume SET full_name = ? WHERE uuid = ?")) {
                ps.setString(1, r.getFullName());
                ps.setString(2, r.getUuid());
                if (ps.executeUpdate() != 1) {
                    throw new NotExistStorageException(r.getUuid());
                }
            }
            deleteContacts(conn, r);
            deleteSections(conn, r);
            insertContact(conn, r);
            insertSections(conn, r);
            return null;
        });
    }

    @Override
    public void save(Resume r) {
        sqlHelper.transactionalExecute(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO resume (uuid, full_name) VALUES (?,?)")) {
                ps.setString(1, r.getUuid());
                ps.setString(2, r.getFullName());
                ps.execute();
            }
            insertContact(conn, r);
            insertSections(conn, r);
            return null;
        });
    }

    @Override
    public void delete(String uuid) {
        sqlHelper.execute("DELETE FROM resume WHERE uuid=?", ps -> {
            ps.setString(1, uuid);
            if (ps.executeUpdate() == 0) {
                throw new NotExistStorageException(uuid);
            }
            return null;
        });
    }

    @Override
    public List<Resume> getAllSorted() {
        Map<String, Resume> resumeMap = sqlHelper.execute("SELECT * FROM resume r ORDER BY full_name, uuid", ps -> {
            ResultSet rs = ps.executeQuery();
            Map<String, Resume> map = new LinkedHashMap<>();
            while (rs.next()) {
                String uuid = rs.getString("uuid");
                map.put(uuid, new Resume(uuid, rs.getString("full_name")));
            }
            return map;
        });

        sqlHelper.execute("SELECT * FROM contact c", ps -> {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String uuid  = rs.getString("resume_uuid");
                Resume r = resumeMap.get(uuid);
                r.addContact(ContactType.valueOf(rs.getString("type")), rs.getString("value"));
            }
            return null;
        });

        sqlHelper.execute("SELECT * FROM section s", ps -> {
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                String uuid  = rs.getString("resume_uuid");
                Resume r = resumeMap.get(uuid);
                SectionType sectionType = SectionType.valueOf(rs.getString("type_s"));
                switch (sectionType) {
                    case PERSONAL:
                    case OBJECTIVE:
                        r.addSection(sectionType, new TextSection(rs.getString("value_s")));
                        break;
                    case ACHIEVEMENT:
                    case QUALIFICATIONS:
                        List<String> values = Arrays.asList(rs.getString("value_s").split("\n"));
                        r.addSection(sectionType, new ListSection(values));
                        break;
                    case EXPERIENCE:
                    case EDUCATION:
                        break;
                }

            }
            return null;
        });

        return new ArrayList<>(resumeMap.values());
    }

    @Override
    public int size() {
        return sqlHelper.execute("SELECT count(*) FROM resume", st -> {
            ResultSet rs = st.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        });
    }

    private void insertContact(Connection conn, Resume r) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO contact (resume_uuid, type, value) VALUES (?,?,?)")) {
            for (Map.Entry<ContactType, String> e : r.getContacts().entrySet()) {
                ps.setString(1, r.getUuid());
                ps.setString(2, e.getKey().name());
                ps.setString(3, e.getValue());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void insertSections(Connection conn, Resume r) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO section (resume_uuid, value_s, type_s) VALUES (?,?,?)")) {
            for (Map.Entry<SectionType, Section> e : r.getSections().entrySet()) {
                switch (e.getKey()) {
                    case PERSONAL:
                    case OBJECTIVE:
                        ps.setString(1, r.getUuid());
                        ps.setString(2, ((TextSection) e.getValue()).getContent());
                        ps.setString(3, e.getKey().name());
                        break;
                    case ACHIEVEMENT:
                    case QUALIFICATIONS:
                        ps.setString(1, r.getUuid());
                        ps.setString(2, String.join("\n", ((ListSection) e.getValue()).getItems()));
                        ps.setString(3, e.getKey().name());
                        break;
                    case EXPERIENCE:
                    case EDUCATION:
                        break;
                }
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void deleteContacts(Connection conn, Resume r) {
        sqlHelper.execute("DELETE  FROM contact WHERE resume_uuid=?", ps -> {
            ps.setString(1, r.getUuid());
            ps.execute();
            return null;
        });
    }

    private void deleteSections(Connection conn, Resume r) {
        sqlHelper.execute("DELETE FROM section WHERE resume_uuid=?", ps -> {
            ps.setString(1, r.getUuid());
            ps.execute();
            return null;
        });
    }

    private void addContact(ResultSet rs, Resume r) throws SQLException {
        String value = rs.getString("value");
        if (value != null) {
            r.addContact(ContactType.valueOf(rs.getString("type")), value);
        }
    }

    private void addSection(ResultSet rs, Resume r) throws SQLException {
        String value_s = rs.getString("value_s");
        if (value_s != null) {
            SectionType sectionType = SectionType.valueOf(rs.getString("type_s"));
            String value = rs.getString("value_s");
            switch (sectionType) {
                case PERSONAL:
                case OBJECTIVE:
                    r.addSection(sectionType, new TextSection(value));
                    break;
                case ACHIEVEMENT:
                case QUALIFICATIONS:
                    List<String> values = Arrays.asList(value.split("\n"));
                    r.addSection(sectionType, new ListSection(values));
                    break;
                case EXPERIENCE:
                case EDUCATION:
                    break;
            }
        }
    }
}
