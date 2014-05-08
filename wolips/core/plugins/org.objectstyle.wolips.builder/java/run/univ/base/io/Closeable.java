/* Closeable.java
 * Created on 21 avr. 07
 */
package run.univ.base.io;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import run.univ.base.collections.AbstractIterator;
//import run.univ.base.log.Log;

/**
 * Quelques outils pour fermer des objets "fermables" sans jeter d'exception.
 * 
 * @author jclain
 */
public class Closeable {
//    private static final Log log = Log.getLog(Closeable.class);

    private static final void cannotClose(Exception e) {
//        log.error("Erreur ignorée lors de la fermeture de la resource", e);
    }

    // ---------------------------------------------------------------
    // Flux

    /** Fermer un flux, sans lancer d'exception. */
    public static final void close(java.io.Closeable input) {
        try {
            if (input != null) input.close();
        } catch (IOException e) {
            cannotClose(e);
        }
    }

    public static final void close(Socket socket) {
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            cannotClose(e);
        }
    }

    public static final void close(ServerSocket serverSocket) {
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            cannotClose(e);
        }
    }

    // ---------------------------------------------------------------
    // JDBC

    public static final void close(Statement statement) {
        try {
            if (statement != null) statement.close();
        } catch (SQLException e) {
            cannotClose(e);
        }
    }

    public static final void close(PreparedStatement preparedStatement) {
        try {
            if (preparedStatement != null) preparedStatement.close();
        } catch (SQLException e) {
            cannotClose(e);
        }
    }

    public static final void close(ResultSet resultSet) {
        try {
            if (resultSet != null) resultSet.close();
        } catch (SQLException e) {
            cannotClose(e);
        }
    }

    public static final void close(Connection connection) {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
            cannotClose(e);
        }
    }

    // ---------------------------------------------------------------
    // Iterateurs

    public static final void close(NamingEnumeration<?> namingEnumeration) {
        try {
            if (namingEnumeration != null) namingEnumeration.close();
        } catch (NamingException e) {
            cannotClose(e);
        }
    }

    public static final void close(Iterator<?> iterator) {
        try {
            if (iterator != null && iterator instanceof AbstractIterator) {
                ((AbstractIterator<?>)iterator).close();
            }
        } catch (Exception e) {
            cannotClose(e);
        }
    }
}
