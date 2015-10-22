package com.base1.sql;

import java.sql.*;
import java.util.ArrayList;

import com.base1.grafo.CaracteristicaGrafo;
import com.base1.grafo.Valor;
import com.base1.misc.Caracteristica;
import com.base1.misc.Respuesta;
import com.mysql.jdbc.ConnectionPropertiesTransform;

/**
 * Esta clase tiene todas las consultas necesarias que cliente genio necesita,
 * fue basada en el codigo de esta pagina
 * http://www.tutorialspoint.com/jdbc/jdbc-sample-code.htm
 * 
 * @author exequiel
 *
 */
public class Conslutor {
	private String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private String url = "jdbc:mysql://127.0.0.1:3306/db_genio";
	private String user = "root";
	private String password = "root";
	private Connection connection;
	private String consultaRespuestaPorTopico = "select respuesta.id_respuesta, respuesta.ranking, caracteristica.caracteristica, caracteristica.valor, caracteristica.peso, caracteristica.pregunta from respuesta left join caracteristica on caracteristica.id_caracteristica = respuesta.id_respuesta where respuesta.id_topico = ? order by respuesta.id_respuesta";
	private String consultaActualizarRespuesta = "update respuesta set ranking = ? where id_respuesta = ?";
	private String consultaActualizarCaracteristica = "update caracteristica set peso = ? where id_caracteristica = ? and caracteristica = ?";
	private String consultaPorCaracteristicas = "select distinct caracteristica from caracteristica where  id_caracteristica in (select id_respuesta from respuesta where id_topico = ?)";
	private String consultaPorPropsDeCarac = "select distinct valor, pregunta, peso from caracteristica where caracteristica = ?";

	public Conslutor() {
		try {
			Class.forName(JDBC_DRIVER).newInstance();
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void conectar() {
		try {
			connection = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("no se pudo conectar");
			e.printStackTrace();
		}
	}

	public void cerrar() {
		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ResultSet consulta(String unaConsulta) {
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(unaConsulta);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return resultSet;
	}

	public ArrayList<Respuesta> consultarPorRespuestas(String topico) {
		this.conectar();
		ArrayList<Respuesta> respuestas = new ArrayList<Respuesta>();
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		Respuesta respAux = null;
		try {
			statement = connection.prepareStatement(consultaRespuestaPorTopico);
			statement.setString(1, "materia");
			resultSet = statement.executeQuery();

			while (resultSet.next()) {
				String id_respuesta = resultSet.getString("id_respuesta");
				int ranking = resultSet.getInt("ranking");
				String caracteristica = resultSet.getString("caracteristica");
				String valor = resultSet.getString("valor");
				String pregunta = resultSet.getString("pregunta");
				int peso = resultSet.getInt("peso");
				// Si la respuesta no esta insertada insertar, si esta
				// insertada agregar caracteristica
				if (respAux != null) {
					// Si sigo obteniendo datos de una respuesta simplemente
					// agrego caracteristica
					if (respAux.getId_respuesta().equals(id_respuesta)) {
						respAux.setCaracteristica(new Caracteristica(
								caracteristica, valor, pregunta, peso));
					} else {
						// si obtengo una nueva respues guardo la anterior y
						// creo esta nueva respuesta
						respuestas.add(respAux);
						respAux = new Respuesta(topico, id_respuesta, ranking);
						respAux.setCaracteristica(new Caracteristica(
								caracteristica, valor, pregunta, peso));
					}
				} else {
					// No tengo una primera respuesta
					respAux = new Respuesta(topico, id_respuesta, ranking);
					respAux.setCaracteristica(new Caracteristica(
							caracteristica, valor, pregunta, peso));
				}
			}
			// Agrego la ultima respuesta
			respuestas.add(respAux);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				statement.close();
				resultSet.close();
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return respuestas;

	}

	public ArrayList<CaracteristicaGrafo> consultaCaracteristicasG(String topico) {
		ArrayList<CaracteristicaGrafo> csg = new ArrayList<CaracteristicaGrafo>();

		ArrayList<String> cString = new ArrayList<String>();
		PreparedStatement statment = null;
		ResultSet resultSet = null;

		this.conectar();

		try {
			statment = connection.prepareStatement(consultaPorCaracteristicas);
			statment.setString(1, topico);
			resultSet = statment.executeQuery();

			while (resultSet.next()) {
				String caracAux = resultSet.getString("caracteristica");
				cString.add(caracAux);

			}

			for (String caracteristica : cString) {
				statment = connection.prepareStatement(consultaPorPropsDeCarac);
				statment.setString(1, caracteristica);
				resultSet = statment.executeQuery();
				CaracteristicaGrafo cg = new CaracteristicaGrafo(caracteristica);
				while (resultSet.next()) {
					String etiqueta = resultSet.getString("valor");
					String pregunta = resultSet.getString("pregunta");
					int peso = resultSet.getInt("peso");
					
					cg.setValor(new Valor(etiqueta, pregunta, peso));
				}
				csg.add(cg);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				statment.close();
				resultSet.close();
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return csg;
	}

	public void actualizarDatos(ArrayList<Respuesta> respuestas) {
		this.conectar();
		PreparedStatement sttmntRespuesta = null;
		PreparedStatement sttmntCaracteristica = null;

		try {
			sttmntRespuesta = connection
					.prepareStatement(consultaActualizarRespuesta);
			sttmntCaracteristica = connection
					.prepareStatement(consultaActualizarCaracteristica);
			for (Respuesta respuesta : respuestas) {
				sttmntRespuesta.setInt(1, respuesta.getRanking());
				sttmntRespuesta.setString(2, respuesta.getId_respuesta());
				sttmntRespuesta.executeUpdate();
				for (Caracteristica caracteristica : respuesta
						.getCaracteristicas()) {
					sttmntCaracteristica.setInt(1, caracteristica.getPeso());
					sttmntCaracteristica.setString(2,
							respuesta.getId_respuesta());
					sttmntCaracteristica.setString(3,
							caracteristica.getVariable());
					sttmntCaracteristica.executeUpdate();
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			try {
				sttmntCaracteristica.close();
				sttmntRespuesta.close();
				connection.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}
}
