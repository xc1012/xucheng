
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;


public class demo00 {
    public static void main(String[] args) throws Exception {
        int port=8080;
        ServerSocket server=new ServerSocket(port);
        //自定端口，创建服务端Socket
        System.out.println("开始监听，端口号："+port);
        while(true){
            Socket client=server.accept();
            //监听获取客户端请求socket，accept方法是一个阻塞方法，在客户端和服务端之间建立联系之前一直等待阻塞
            System.out.println("Incoming!!!");

            //获取套接字的输入和输出
            InputStream inp=client.getInputStream();
            DataOutputStream outp = new DataOutputStream(client.getOutputStream());

            BufferedReader br = new BufferedReader(new InputStreamReader(inp));


            String requestLine = br.readLine();
            System.out.println(requestLine);

            String headerLine = null;
            while((headerLine = br.readLine()).length()!=0){
                System.out.println(headerLine);
            }


            StringTokenizer tokens = new StringTokenizer(requestLine);
            tokens.nextToken();
            String fileName = tokens.nextToken();


            fileName = "." + fileName;

            String statusLine = null;
            String contentTypeLine = null;
            String entityBody = null;
            try {

                FileInputStream fis =null;
                fis = new FileInputStream(fileName);

                statusLine = "HTTP/1.0 200 OK" + "\r\n";
                if(fileName.endsWith(".htm")||fileName.endsWith(".html")) {
                    contentTypeLine = "Content-Type:" + "text/html" + "\r\n";
                }else if(fileName.endsWith(".jpg")||fileName.endsWith(".jpeg")) {
                    contentTypeLine = "Content-Type:" + "image/jpeg" + "\r\n";
                }else {
                    contentTypeLine = "Content-Type:" + "application/ocatet-stream" + "\r\n";
                }


                outp.writeBytes(statusLine);

                outp.writeBytes(contentTypeLine);

                outp.writeBytes("\r\n");


                byte[] arr = new byte[1024];
                int len = 0;
                while((len = fis.read(arr))!= -1) {
                    outp.write(arr, 0, len);
                }

                fis.close();
                outp.close();
                br.close();
                client.close();

            }catch(FileNotFoundException e){

                statusLine = "HTTP/1.0 404 Not Found"+"\r\n";
                contentTypeLine = "Content-Type:text/html"+"\r\n";
                entityBody = "<html>" + "<head><title>Not Found</title></head>" + "<body>Not Found</body></html>";

                outp.writeBytes(statusLine);

                outp.writeBytes(contentTypeLine);

                outp.writeBytes("\r\n");

                outp.writeBytes(entityBody);

                outp.close();
                br.close();
                client.close();

            }
        }
    }
}
