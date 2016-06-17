package net.thecobix.stats.client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.bukkit.Bukkit;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.internal.Streams;

import net.thecobix.openwsk.main.OpenWSK;

public class Client implements Runnable {

	private Socket client;
	private OpenWSK pl;
	private int port;
	private String host;
	public long now = System.currentTimeMillis();
	public int ping = 0;
	public long pStart;
	public boolean sended = false;
	public Thread keepAliveThread;
	
	public Client(String host ,int port, OpenWSK pl) {
		this.pl = pl;
		this.port = port;
		this.host = host;
	}

	@Override
	public void run() {
		try {
			this.client = new Socket(this.host, this.port);
			send("COMMAND: username WSK_"+Bukkit.getServerName());
			keepAliveThread = new Thread(new KeepAliveRunnable(this));
			keepAliveThread.start();
			while(client.isConnected()) {
				try {
					InputStream in = this.client.getInputStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(in));
					String line = br.readLine();
					if(line != null) {
						if(line.startsWith("COMMAND:")) {
							pl.getCmdManager().runCommands(line, this);
						}
					}
				}catch(IOException r) {
					disconnect();
				}
			}
		} catch (UnknownHostException e) {
			System.out.println("Connection failed. Unknown host");
		} catch (IOException e) {
			System.out.println("Error:");
			e.printStackTrace();
		} catch(Exception e) {
			this.disconnect();
		}
	}
	
	public void send(String msg) throws IOException {
		OutputStream out = this.client.getOutputStream();
		PrintWriter pw = new PrintWriter(out, true);
		pw.println(msg);
	}
	
	public boolean isConnected() {
		return this.client.isConnected() && this.client.isClosed() == false;
	}
	
	public void disconnect() {
		try {
			keepAliveThread.interrupt();
			send("COMMAND: disconnect");
			this.client.close();
			pl.t.interrupt();
		} catch (IOException e) {
		}
	}
	
}
