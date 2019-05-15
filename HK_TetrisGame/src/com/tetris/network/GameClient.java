package com.tetris.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import com.tetris.window.Tetris;

//---------------------[ Ŭ���̾�Ʈ ]---------------------
public class GameClient implements Runnable{
	private Tetris tetris;
	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;

	//���� IP
	private String ip;
	private int port;
	private String name;
	private int index;
	private boolean isPlay;
	
	//������
	public GameClient(Tetris tetris,String ip, int port, String name){
		this.tetris = tetris;
		this.ip = ip;
		this.port = port;
		this.name = name;
	}//GameClient()

	public boolean start(){
		return this.execute();	
	}

	//���� & IO ó��
	public boolean execute(){
		try{
			socket = new Socket(ip,port);
			ip = InetAddress.getLocalHost().getHostAddress();
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			System.out.println("Ŭ���̾�Ʈ�� ���� ���Դϴ�.");
		}catch(UnknownHostException e){
			e.printStackTrace();
			return false;
		}catch(IOException e){
			e.printStackTrace();
			return false;
		}

		tetris.getBoard().clearMessage();
		
		//�̸�������
		DataShip data = new DataShip();
		data.setIp(ip);
		data.setName(name);
		send(data);
		
		//����Ʈ�޾ƿ���
		printSystemMessage(DataShip.PRINT_SYSTEM_OPEN_MESSAGE);
		//����Ʈ�� �߰��ϱ�
		printSystemMessage(DataShip.PRINT_SYSTEM_ADDMEMBER_MESSAGE);
		//�ε����޾ƿ���
		setIndex();
		//������
		Thread t = new Thread(this);
		t.start();
		
		return true;
	}//execute()

	
	//Run : ������ ����� ��ٸ�.
	public void run(){
		DataShip data = null;
		while(true){
			try{
				data = (DataShip)ois.readObject(); 
			}catch(IOException e){e.printStackTrace();break;
			}catch(ClassNotFoundException e){e.printStackTrace();}


			//�����κ��� DataShip Object�� �޾ƿ�.
			if(data == null) continue;
			if(data.getCommand() == DataShip.CLOSE_NETWORK){
				reCloseNetwork();
				break;
			}else if(data.getCommand() == DataShip.SERVER_EXIT){
				closeNetwork(false);
			}else if(data.getCommand() == DataShip.GAME_START){
				reGameStart(data.isPlay(), data.getMsg(), data.getSpeed());
			}else if(data.getCommand() == DataShip.ADD_BLOCK){
				if(isPlay)reAddBlock(data.getMsg(), data.getNumOfBlock(), data.getIndex());
			}else if(data.getCommand() == DataShip.SET_INDEX){
				reSetIndex(data.getIndex());
			}else if(data.getCommand() == DataShip.GAME_OVER){
				if(index == data.getIndex()) isPlay = data.isPlay();
				reGameover(data.getMsg(), data.getTotalAdd());
			}else if(data.getCommand() == DataShip.PRINT_MESSAGE){
				rePrintMessage(data.getMsg());
			}else if(data.getCommand() == DataShip.PRINT_SYSTEM_MESSAGE){
				rePrintSystemMessage(data.getMsg());
			}else if(data.getCommand() == DataShip.GAME_WIN){
				rePrintSystemMessage(data.getMsg()+"\nTOTAL ADD : "+data.getTotalAdd());
				tetris.getBoard().setPlay(false);
			}
			
		}//while(true)
		
		
	}//run()


	// �������� ��û��
	public void send(DataShip data){
		try{
			oos.writeObject(data); 
			oos.flush();
		}catch(IOException e){
			e.printStackTrace();
		}
	}//sendData()
	
	
	
	
	
	//��û�ϱ� : �������
	public void closeNetwork(boolean isServer){
		DataShip data = new DataShip(DataShip.CLOSE_NETWORK);
		if(isServer) data.setCommand(DataShip.SERVER_EXIT);
		send(data);
	}
	//�����ϱ� : �������
	public void reCloseNetwork(){

		tetris.closeNetwork();
		try {
			ois.close();
			oos.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//��û�ϱ� : ���ӽ���
	public void gameStart(int speed){
		DataShip data = new DataShip(DataShip.GAME_START);
		data.setSpeed(speed);
		send(data);
	}
	//�����ϱ� : ���ӽ���
	public void reGameStart(boolean isPlay, String msg, int speed){
		this.isPlay = isPlay;
		tetris.gameStart(speed);
		rePrintSystemMessage(msg);
	}
	//��û�ϱ� : �޽���
	public void printSystemMessage(int cmd){
		DataShip data = new DataShip(cmd);
		send(data);
	}
	//�����ϱ� : �޽���
	public void rePrintSystemMessage(String msg){
		tetris.printSystemMessage(msg);
	}
	public void addBlock(int numOfBlock){
		DataShip data = new DataShip(DataShip.ADD_BLOCK);
		data.setNumOfBlock(numOfBlock);
		send(data);
	}
	public void reAddBlock(String msg, int numOfBlock, int index){
		if(index != this.index)tetris.getBoard().addBlockLine(numOfBlock);
		rePrintSystemMessage(msg);
	}
	
	
	public void setIndex(){
		DataShip data = new DataShip(DataShip.SET_INDEX);
		send(data);
	}
	public void reSetIndex(int index){
		this.index = index;
	}
	
	//��û�ϱ� : ��������
	public void gameover(){
		DataShip data = new DataShip(DataShip.GAME_OVER);
		send(data);
	}
	public void reGameover(String msg, int totalAdd){
		tetris.printSystemMessage(msg);
		tetris.printSystemMessage("TOTAL ADD : "+totalAdd);
	}
	public void printMessage(String msg){
		DataShip data = new DataShip(DataShip.PRINT_MESSAGE);
		data.setMsg(msg);
		send(data);
	}
	public void rePrintMessage(String msg){
		tetris.printMessage(msg);
	}
	public void reChangSpeed(Integer speed) {
		tetris.changeSpeed(speed);
	}
}
