package com.tetris.controller;

import com.tetris.classes.Block;
import com.tetris.classes.TetrisBlock;

public class TetrisController {

	private int rotation_index;
	private TetrisBlock block;
	private Block[][] map;
	
	private int maxX, maxY;
	
	/**
	 * ��Ʈ���� ���� �����ϴ� ��Ʈ�ѷ��̴�.
	 * 
	 * @param block : ������ ��Ʈ���� ��
	 * @param minX : ���� ������ �ּ� GridX��ǥ
	 * @param minY : ���� ������ �ּ� GridY��ǥ
	 * @param maxX : ���� ������ �ִ� GridX��ǥ
	 * @param maxY : ���� ������ �ִ� GridY��ǥ
	 */
	public TetrisController(TetrisBlock block, int maxX, int maxY, Block[][] map) {
		this.block = block;
		
		this.maxX = maxX;
		this.maxY = maxY;
		
		this.map = map;
		this.rotation_index = block.getRotationIndex();
		
	}
	
	
	/**
	 * ������ ��Ʈ���� ���� �Ѱ��ش�.
	 * @param block ������ ��Ʈ���� ��
	 */
	public void setBlock(TetrisBlock block){
		this.block = block;
		this.rotation_index = block.getRotationIndex();
	}
	
	
	/**
	 * ���� ��ǥ�� ����Ѵ�.
	 */
	public void showIndex(){
		for(Block blocks : block.getBlock()){
			if(blocks!=null)System.out.print("("+blocks.getX()+","+blocks.getY()+")");
		}
		System.out.println();
	}
	
	
	/**
	 * ���� ��ǥ �����ȿ� �ִ��� Ȯ���Ѵ�.
	 * 
	 * @param maxX : ���� ������ �� �ִ� GridX��ǥ ����
	 * @param maxY : ���� ������ �� �ִ� GridY��ǥ ����
	 * @return
	 */
	public boolean checkIndex(int maxX, int maxY){
		for(Block blocks : block.getBlock()){
			if(blocks==null || blocks.getY()<0) continue;
			
			if(blocks.getX() < 0 || blocks.getY() < 0 
					|| blocks.getX() >= maxX || blocks.getY() >= maxY )
				return false;
			else{
				if(map[blocks.getY()][blocks.getX()]!=null)return false;
			}
		}
		return true;
	}
	
	/**
	 * �������� �̵�
	 * default 1ĭ
	 */
	public void moveLeft(){moveLeft(1);}
	public void moveLeft(int x){
		//�̵�
		block.moveLeft(x);
				
		//üũ, ������ ����ٸ� ���󺹱�
		if(!checkIndex(maxX,maxY)) {
			block.moveLeft(-x);
		}
	}
	
	/**
	 * ���������� �̵�
	 * default 1ĭ
	 */
	public void moveRight(){moveRight(1);}
	public void moveRight(int x){
		// �̵�
		block.moveRight(x);
		
				
		// üũ, ������ ����ٸ� ���󺹱�
		if (!checkIndex(maxX, maxY)) {
			block.moveRight(-x);
		}
	}
	
	
	/**
	 * �Ʒ��� �̵�
	 * default 1ĭ
	 */
	public boolean moveDown(){return moveDown(1);}
	public boolean moveDown(int y){
		
		boolean moved = true;
		// �̵�
		block.moveDown(y);
		// üũ, ������ ����ٸ� ���󺹱�
		if (!checkIndex(maxX, maxY)) {
			block.moveDown(-y);
			moved = false;
		}
		return moved;
	}
	
	/**
	 * 
	 * @param startY ���� ���� ��ġ
	 * @param moved ����Լ��� �ʿ��� ���ڷ�, ������ true�� �Ѵ�.
	 * @return	moveQuickDown�� �ٽ� ȣ���Ѵ�.
	 */
	public boolean moveQuickDown(int startY, boolean moved){
		
		// �̵�
		block.moveDown(1);
		// üũ, ������ ����ٸ� ���󺹱�
		if (!checkIndex(maxX, maxY)) {
			block.moveDown(-1);
			if(moved) return false;
		}
		return moveQuickDown(startY+1, true);
	}
	
	
	
	/**
	 * ��Ʈ���� ���� ȸ����Ų��.
	 * @param rotation_direction : ȸ������
	 * TetrisBlock.ROTATION_LEFT(�ð����), TetrisBlock.ROTATION_RIGHT(�ݽð����)
	 */
	public void nextRotation(int rotation_direction){
		if(rotation_direction == TetrisBlock.ROTATION_LEFT) 
			this.nextRotationLeft();
		else if(rotation_direction == TetrisBlock.ROTATION_RIGHT) 
			this.nextRotationRight();
	}
	
	
	/**
	 * ��Ʈ���� ���� ȸ����Ų��. (�ð����)
	 * ���� ȸ���� ������ �����, ȸ���� ���� �ʴ´�.
	 */
	public void nextRotationLeft(){
		//ȸ��
		rotation_index++;
		if(rotation_index == TetrisBlock.ROTATION_270+1) rotation_index = TetrisBlock.ROTATION_0;
		block.rotation(rotation_index);
		
		//üũ, ������ ����ٸ� ���󺹱�
		if(!checkIndex(maxX,maxY)) {
			rotation_index--;
			if(rotation_index == TetrisBlock.ROTATION_0-1) rotation_index = TetrisBlock.ROTATION_270;
			block.rotation(rotation_index);
		}
	}
	
	
	/**
	 * ��Ʈ���� ���� ȸ����Ų��. (�ݽð����)
	 * ���� ȸ���� ������ �����, ȸ���� ���� �ʴ´�.
	 */
	public void nextRotationRight(){
		//ȸ��
		rotation_index--;
		if(rotation_index == TetrisBlock.ROTATION_0-1) rotation_index = TetrisBlock.ROTATION_270;
		block.rotation(rotation_index);
		
		//üũ, ������ ����ٸ� ���󺹱�
		if(!checkIndex(maxX,maxY)) {
			rotation_index++;
			if(rotation_index == TetrisBlock.ROTATION_270+1) rotation_index = TetrisBlock.ROTATION_0;
			block.rotation(rotation_index);
		}
	}
}
