package ru.velesov7493.products.stores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.velesov7493.products.components.DbUtils;
import ru.velesov7493.products.dto.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcProductsStore implements Store<Integer, Product> {

    private static final class Holder {
        private static final JdbcProductsStore INSTANCE = new JdbcProductsStore();
    }

    private static final Logger LOG = LoggerFactory.getLogger(JdbcProductsStore.class);

    private final DbUtils db;

    private JdbcProductsStore() {
        db = DbUtils.getInstance();
    }

    public static Store<Integer, Product> getInstance() {
        return Holder.INSTANCE;
    }

    private boolean update(Product value) {
        boolean result = false;
        String sql =
            "UPDATE tz_products SET name = ? WHERE id=?;";
        try (
                Connection cn = db.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)
        ) {
            ps.setString(1, value.getName());
            ps.setInt(2, value.getId());
            result = ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOG.error("Ошибка измнения товара с id={}: ", value.getId(), ex);
        }
        return result;
    }

    private boolean create(Product value) {
        boolean result = false;
        String sql =
            "INSERT INTO tz_products (name) VALUES (?);";
        try (
                Connection cn = db.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, value.getName());
            result = ps.execute();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                value.setId(keys.getInt(1));
            }
            keys.close();
        } catch (SQLException ex) {
            LOG.error("Ошибка создания товара: ", ex);
        }
        return result;
    }

    @Override
    public List<Product> findAll() {
        List<Product> result = new ArrayList<>();
        String sql =
            "SELECT p.id, p.name, c.name AS category FROM tz_products AS p "
            + "INNER JOIN tz_categories AS c ON p.id_category=c.id;";
        try (
            Connection cn = db.getConnection();
            Statement s = cn.createStatement();
        ) {
            ResultSet rs = s.executeQuery(sql);
            while (rs.next()) {
                Product entry = new Product();
                entry.setId(rs.getInt("id"));
                entry.setName(rs.getString("name"));
                entry.setCategoryName(rs.getString("category"));
                result.add(entry);
            }
        } catch (SQLException ex) {
            LOG.error("Ошибка получения списка товаров: ", ex);
        }
        return result;
    }

    @Override
    public Product findById(Integer id) {
        Product result = null;
        String sql =
            "SELECT p.id, p.name, c.name AS category FROM tz_products AS p "
            + "INNER JOIN tz_categories AS c ON p.id_category=c.id AND p.id=?;";
        try (
            Connection cn = db.getConnection();
            PreparedStatement ps = cn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = new Product();
                result.setId(rs.getInt("id"));
                result.setName(rs.getString("name"));
                result.setCategoryName(rs.getString("category"));
            }
        } catch (SQLException ex) {
            LOG.error("Ошибка получения товара с id={}: ", id, ex);
        }
        return result;
    }

    @Override
    public boolean save(Product value) {
        return value.getId() == 0 ? create(value) : update(value);
    }

    @Override
    public boolean deleteById(Integer id) {
        boolean result = false;
        String sql =
            "DELETE FROM tz_products WHERE id=?;";
        try (
            Connection cn = db.getConnection();
            PreparedStatement ps = cn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            result = ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOG.error("Ошибка удаления товара с id={}: ", id, ex);
        }
        return result;
    }
}