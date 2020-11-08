import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;


public class TcpServerSimpleNB {

	static int           port_ = 21596; //my port number
	static ServerSocket  server_;
	static int           sleepTime_ = 100; // 10 seconds to allow for larger messages
	static int           timeTillError_ = 10000; // milliseconds
	static int           bufferSize_ = 256; // the maximum size needed for requirement 2
	static int           soTimeout_ = 10; // 10 ms
	static String        filePath = "/cs/home/mcd6/nginx_default/cs2003-net1"; //Standard beginning of all file'text path

	public static void main(String[] args) {
		startServer();

		while(!server_.isClosed()){
			try {
				Socket       connection;
				InputStream  rx; //InputStream for recieving the message from the client
				OutputStream tx; //Output stream for sending the signal to the client to make sure the server is still running

				connection = server_.accept(); // waits for connection
				rx = connection.getInputStream();
				tx = connection.getOutputStream();

				System.out.println("New connection ... " +
						connection.getInetAddress().getHostName() + ":" +
						connection.getPort());

				byte[] buffer = new byte[bufferSize_];
				int b = 0;
				while (b < 1) {
					Thread.sleep(sleepTime_);

					buffer = new byte[bufferSize_];
					b = rx.read(buffer);
					if (b == -1) { //If  b = -1 that means the client has disconnected and thus an error message should be displayed
						System.out.println("The client has disconnected.");
						System.exit(0);
					}
				}

				String text = null;

				if (b > 0) {
					text = new String(buffer);
					System.out.println("Received " + b + " bytes --> " + text);

					tx.write(buffer, 0, b); // send signal back to client

					connection.close(); // finished
				}

				Date date = new Date(); //Gets the date
				SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd"); //gets the format for the folder names
				SimpleDateFormat currentTime = new SimpleDateFormat("HH-mm-ss.SSS"); //gets the format for the file names
				String folderString = currentDate.format(date); //creates a string for the folder name
				String fileString = currentTime.format(date); //creates a string for the file name

				File folder = new File(filePath + File.separator + folderString); //creates a folder with the required file path

				if (!folder.exists()) { //if the folder doesn't exist, it's created
					folder.mkdir();
					System.out.println("++ Created directory: " + folderString);
				}

				File file = new File(filePath + File.separator + folderString + File.separator + fileString); //creates a file with
				//required file path
				try {
					//filewriter sends the text to the file before being closed
					FileWriter fw = new FileWriter(file);
					fw.write(text);
					fw.flush();
					fw.close();
				}
				catch (IOException e) {
					System.out.println("IOException - write(): " + e.getMessage());
				}

				System.out.println("++ Wrote \"" + text + "\" to file: " + fileString);

			}catch (SocketTimeoutException e) {
				// no incoming data
			}
			catch (InterruptedException e) {
				System.err.println("Interrupted Exception: " + e.getMessage());
			}
			catch (IOException e) {
				System.err.println("IO Exception: " + e.getMessage());
			}
		}
	}

	public static void startServer() {
		try {
			server_ = new ServerSocket(port_); // make a socket
			System.out.println("--* Starting server " + server_.toString());
		}catch (IOException e) {
			System.err.println("IO Exception: " + e.getMessage());
		}
	}
}
