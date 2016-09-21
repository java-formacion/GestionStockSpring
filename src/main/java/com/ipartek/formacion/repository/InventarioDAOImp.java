package com.ipartek.formacion.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.ipartek.formacion.domain.Product;
import com.ipartek.formacion.repository.mapper.ProductMapper;

@Repository("inventarioDAOImp")
public class InventarioDAOImp implements InventarioDAO {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(InventarioDAOImp.class);

	@Autowired
	private DataSource dataSource = null;

	private JdbcTemplate jdbctemplate = null;
	private SimpleJdbcCall jdbcCall;

	@Autowired
	@Override
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbctemplate = new JdbcTemplate(this.dataSource);
		this.jdbcCall = new SimpleJdbcCall(this.dataSource);

	}

	@Override
	public void increasePrice(int percentage) {
		// CallableStatement con procedimiento almacenado en BBDD
		logger.trace("Llamando rutina almacenada en Heidi 'incrementar_precio'");
		jdbcCall.withProcedureName("incrementar_precio");

		SqlParameterSource parameterIn = new MapSqlParameterSource().addValue("porcentaje", percentage);

		this.jdbcCall.execute(parameterIn);

		/*
		 * Si tuvieramos parametros de salida 'out', ej: Map<String, Object> out
		 * = jdbcCall.execute(in); out.get("nombre_parametro_salida");
		 * 
		 */

		this.logger.info("Incrementados todos los productos un " + percentage + "%");
	}

	@Override
	public List<Product> getProducts() {

		ArrayList<Product> lista = new ArrayList<Product>();
		final String SQL = "SELECT id, description, price FROM  products;";

		try {
			lista = (ArrayList<Product>) this.jdbctemplate.query(SQL, new ProductMapper());
		} catch (final EmptyResultDataAccessException e) {
			logger.warn("NO existen productos todavia" + SQL);
		} catch (final Exception e) {

		}
		return lista;
	}

	// ***********************************
	// * insertar nuevo producto en BBDD *
	// ***********************************
	/*
	 * @Override public boolean insert(Product p) {
	 * 
	 * if (-1 == p.getId()) {
	 * 
	 * final KeyHolder keyHolder = new GeneratedKeyHolder(); final String sql =
	 * "INSERT INTO `products` (`description`, `price`) VALUES ( ? , ? );";
	 * 
	 * affectedRows = this.jdbcTemplateObject.update(new
	 * PreparedStatementCreator() {
	 * 
	 * @Override public PreparedStatement createPreparedStatement(Connection
	 * conn) throws SQLException { final PreparedStatement ps =
	 * conn.prepareStatement(sqlInsert); ps.setString(1, p.getDescription());
	 * ps.setString(2, p.getPrice());
	 * 
	 * return ps; } }, keyHolder);
	 * 
	 * p.setId(keyHolder.getKey().longValue());
	 * 
	 * return false; } }
	 */

	@Override
	public Product getById(long id) {
		Product p = null;
		// TODO cambiar por PreparedStatement
		final String SQL = "SELECT id, description, price FROM products WHERE id=" + id;

		try {
			p = this.jdbctemplate.queryForObject(SQL, new ProductMapper());

		} catch (EmptyResultDataAccessException e) {
			logger.warn("No existen productos con ID=" + id);
			p = null;
		} catch (Exception e) {
			logger.error(e.getMessage());
			p = null;
		}
		return p;
	}

	@Override
	public boolean eliminar(long id) {
		boolean resul = false;
		// TODO preparedStatement
		final String SQL = "DELETE FROM `products` WHERE  `id`=" + id;

		if (1 == this.jdbctemplate.update(SQL)) {
			resul = true;
		}

		return resul;
	}

	@Override
	public boolean insertar(final Product p) {
		boolean resul = false;
		int affectedRows = -1;
		KeyHolder keyHolder = new GeneratedKeyHolder();

		final String sqlInsert = "INSERT INTO `products` ( `description`, `price`) VALUES ( ? , ? );";
		affectedRows = this.jdbctemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
				final PreparedStatement ps = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, p.getDescription());
				ps.setDouble(2, p.getPrice());

				return ps;
			}
		}, keyHolder);

		if (affectedRows == 1) {
			resul = true;
			p.setId(keyHolder.getKey().longValue());
			this.logger.trace("Producto creado OK");
		}

		return true;
	}

	@Override
	public boolean modificar(Product p) {
		final String SQL = "UPDATE `products` SET `description`=? , `price`=? WHERE  `id`=?;";
		Object[] arguments = { p.getDescription(), p.getPrice(), p.getId() };
		int affectedRows = this.jdbctemplate.update(SQL, arguments);
		return (affectedRows == 1) ? true : false;
	}

}