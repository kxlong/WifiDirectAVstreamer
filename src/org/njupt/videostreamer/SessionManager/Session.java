package org.njupt.videostreamer.SessionManager;

import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Vector;

import org.njupt.videostreamer.utils.AudioProfile;

public abstract class Session {
	private ServerSocket local = null;
	private int clientNum = 0; 
	protected Vector<SocketChannel> newClients = null,leavingClients = null;
	protected Vector<SocketChannel> clients = null;
	private String name = null;
	
	public abstract void init();
	
	public abstract void over();
	
	public abstract void send();
	
	public abstract void receive();
	
	public Vector<SocketChannel> getClientList( ){
		return clients;
	}
	
	public void removeClient(SocketChannel rClient) {
		leavingClients.add(rClient);
	}
	
	public int getCNum(){
		return clientNum;
	}
	
	protected abstract void checkClientList();
}
