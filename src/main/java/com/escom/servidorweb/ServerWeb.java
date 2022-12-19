package com.escom.servidorweb;

import Server.Mime;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataOutputStream;
import java.util.Date;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

public class ServerWeb {
    public static final int PUERTO = 8000;
    private ServerSocket serverSocket;

    public ServerWeb() throws Exception {
        this.serverSocket = new ServerSocket(PUERTO);
    }

    private void start() throws Exception {
        System.out.println("Servidor iniciado en el puerto " + PUERTO + "...");
        while (true) {
            Socket clientSocket = this.serverSocket.accept();
            new ClientHandler(clientSocket).start();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private DataOutputStream dos;
        private Mime mime;
        private DataInputStream dis;

        public ClientHandler(Socket clientSocket) throws Exception {
            this.clientSocket = clientSocket;
            this.dos = new DataOutputStream(clientSocket.getOutputStream());
            this.mime = new Mime();
            this.dis = new DataInputStream(clientSocket.getInputStream());
        }

        public void deleteResource(String resource, String headers) {
            try {
                System.out.println(resource);
                File file = new File(resource);

                if (file.exists()) {
                    if (file.delete()) {
                        System.out.println("------> Archivo " + resource + " eliminado exitosamente\n");

                        String deleteOK = headers +
                            "<html><head><meta charset='UTF-8'><title>202 OK Recurso eliminado</title></head>" +
                            "<body><h1>202 OK Recurso eliminado exitosamente.</h1>" +
                            "<p>El recurso " + resource + " ha sido eliminado permanentemente del servidor." +
                            "Ya no se podra acceder más a él.</p>" +
                            "</body></html>";

                        dos.write(deleteOK.getBytes());
                        dos.flush();
                        System.out.println("Respuesta DELETE: \n" + deleteOK);
                    } else {
                        System.out.println("El archivo " + resource + " no pudo ser borrado\n");

                        String error404 = "HTTP/1.1 404 Not Found\n" +
                            "Date: " + new Date() + " \n" +
                            "Server: EnrikeAbi Server/1.0 \n" +
                            "Content-Type: text/html \n\n" +
                            "<html><head><meta charset='UTF-8'><title>404 Not found</title></head>" +
                            "<body><h1>404 Not found</h1>" +
                            "<p>Archivo " + resource + " no encontrado.</p>" +
                            "</body></html>";

                        dos.write(error404.getBytes());
                        dos.flush();
                        System.out.println("Respuesta DELETE - ERROR 404: \n" + error404);
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        public void sendResource(String resource, int flag) {
            try {
                File file = new File(resource);
                StringBuilder sb = new StringBuilder("HTTP/1.1 200 OK\n");

                if (!file.exists()) {
                    resource = "404.html"; // Recurso no encontrado
                    sb = new StringBuilder("HTTP/1.1 404 Not Found\n");
                } else if (file.isDirectory()) {
                    resource = "403.html"; // Recurso privado
                    sb = new StringBuilder("HTTP/1.1 403 Forbidden\n");
                }

                DataInputStream dis2 = new DataInputStream(new FileInputStream(resource));
                int size = dis2.available();

                // Obtenemos la extensión para determinar el tipo de recurso
                int pos = resource.indexOf(".");
                String extension = resource.substring(pos + 1, resource.length());

                // Enviamos las cabeceras de la respuesta HTTP - MÉTODO HEAD
                sb.append("Date: ").append(new Date()).append(" \n")
                    .append("Server: EnrikeAbi Server/1.0 \n")
                    .append("Content-Type: ").append(mime.get(extension)).append(" \n")
                    .append("Content-Length: ").append(size).append(" \n\n");

                if (flag == 1) {
                    dos.write(sb.toString().getBytes());
                    dos.flush();
                } else {
                    sb.append(dis2.readLine()).append("\n");
                    dos.write(sb.toString().getBytes());
                    dos.flush();
                }

                dis2.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        public void run() {
            try {
                String headers = dis.readLine();
                String[] parts = headers.split(" ");
                String method = parts[0];
                String resource = parts[1];
                int flag = 0;

                while (true) {
                    String line = dis.readLine();
                    if (line.equals("")) {
                        break;
                    }

                    if (line.contains("Connection: keep-alive")) {
                        flag = 1;
                    }
                }

                if (method.equals("DELETE")) {
                    deleteResource(resource, headers);
                } else {
                    sendResource(resource, flag);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            ServerWeb server = new ServerWeb();
            server.start();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}