
import java.io.*;
import java.net.*;

public class TcpClientSimpleNB {
	static int sleepTime_ = 10000; // 10 seconds to allow for larger messages
	static int bufferSize_ =256; // the maximum size needed for requirement 2
	static int soTimeout_ = 10; // milliseconds

	public static void main(String[] args) {
		if (args.length != 2) { // user has not provided arguments
			System.out.println("\n TcpClientSimpleNB <servername> <portnumber> \n");//Error message for invalid command line
			System.exit(0);
		}

		int portNumber = Integer.parseInt(args[1]); //Turns the port number argument into an integer so the value can be checked

		if (!(portNumber > 10000 && portNumber < 65535)) {//Checks to make sure port number is within acceptable range
			System.out.println("Not a valid port number.");
			System.exit(0);
		}

		try {
			Socket       connection;
			InputStream  rx; //InputStream for recieving server message to make sure the server is still running
			OutputStream tx; //Output stream for sending the message to the server
			byte[]       buffer;
			int          b ;

			connection = startClient(args[0], args[1]);
			tx = connection.getOutputStream();
			rx = connection.getInputStream();
			b = 0;

			System.out.print("You have " + (sleepTime_/1000) + " seconds to type something -> ");
			Thread.sleep(sleepTime_); // wait

			buffer = new byte[bufferSize_];
			if (System.in.available() > 0) {
				b = System.in.read(buffer); // keyboard
			}

			if (b > 0) {
				tx.write(buffer, 0, b); // send to server
				System.out.println("Sending " + b + " bytes");
			}

			Thread.sleep(sleepTime_); // wait

			buffer = new byte[bufferSize_];
			b = rx.read(buffer); // from server
			if (b == -1) {//If  b = -1 that means the server has closed and thus an error message should be displayed
				System.out.println("No response from Server. Disconnecting.");
				System.exit(0);
			}

			connection.close();
		}

		catch (SocketTimeoutException e) {
			// no incoming data
		}
		catch (InterruptedException e) {
			System.err.println("Interrupted Exception: " + e.getMessage());
		}
		catch (IOException e) {
			System.err.println("IO Exception: " + e.getMessage());
		}
	} 

	static Socket startClient(String hostname, String portnumber) {
		Socket connection = null;

		try {
			InetAddress address;
			int         port;

			address = InetAddress.getByName(hostname);
			port = Integer.parseInt(portnumber);

			connection = new Socket(address, port); // server
			connection.setSoTimeout(soTimeout_);

			System.out.println("--* Connecting to " + connection.toString());
		}

		catch (IOException e) {
			System.err.println("Error connecting to the specified server.");
			System.exit(0);
		}

		return connection;
	}

}
