package br.edu.ifpb.gugawag.so.sockets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.*;
import java.util.stream.Stream;

public class Servidor2 {

    public static void main(String[] args) throws IOException {

        String HOME = System.getProperty("user.home");

        System.out.println("== Servidor ==");

        // Configurando o socket
        ServerSocket serverSocket = new ServerSocket(7001);
        Socket socket = serverSocket.accept();

        // pegando uma referência do canal de saída do socket. Ao escrever nesse canal, está se enviando dados para o
        // servidor
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        // pegando uma referência do canal de entrada do socket. Ao ler deste canal, está se recebendo os dados
        // enviados pelo servidor
        DataInputStream dis = new DataInputStream(socket.getInputStream());

        Path path = Paths.get(HOME + "/Documents/atividade");


        // laço infinito do servidor
        while (true) {
            System.out.println("Cliente: " + socket.getInetAddress());

            String mensagem = dis.readUTF();
            if (mensagem.contains("readdir")) {
                Stream<Path> streamDeArquivos = Files.walk(path);
                final String[] arquivoDirectorio = {""};
                streamDeArquivos.forEach(arquivo -> {
                    arquivoDirectorio[0] += arquivo.getFileName() + "\n";
                });



                dos.writeUTF(arquivoDirectorio[0]);
            }
            else if (mensagem.contains("rename")) {
                String[] partes = mensagem.split(" ");
                String antigo = partes[1];
                String novo = partes[2];
                File file = new File(path + "/" + antigo);
                boolean result = file.renameTo(new File(path + "/" + novo));
                if (!result) {
                    dos.writeUTF("Erro ao renomear arquivo!");
                }

                dos.writeUTF("Arquivo renomeado com sucesso!");
            }
            else if (mensagem.contains("remove")) {
                String[] partes = mensagem.split(" ");
                String arquivo = partes[1];
                File file = new File(path + "/" + arquivo);
                boolean result = file.delete();
                if (!result) {
                    dos.writeUTF("Erro ao remover arquivo!");
                }

                dos.writeUTF("Arquivo removido com sucesso!");
            }
            else if (mensagem.contains("create")) {
                String[] partes = mensagem.split(" ");
                String arquivo = partes[1];
                File file = new File(path + "/" + arquivo);
                boolean result = file.createNewFile();
                if (!result) {
                    dos.writeUTF("Erro ao criar arquivo!");
                }
                dos.writeUTF("Arquivo criado com sucesso!");
            }

            else {
                dos.writeUTF("Comando inválido!");

            }
        }
        /*
         * Observe o while acima. Perceba que primeiro se lê a mensagem vinda do cliente (linha 29, depois se escreve
         * (linha 32) no canal de saída do socket. Isso ocorre da forma inversa do que ocorre no while do Cliente2,
         * pois, de outra forma, daria deadlock (se ambos quiserem ler da entrada ao mesmo tempo, por exemplo,
         * ninguém evoluiria, já que todos estariam aguardando.
         */
    }
}
