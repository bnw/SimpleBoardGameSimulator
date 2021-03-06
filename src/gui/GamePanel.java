package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MoveAction;

import gameObjects.GameAction;
//import gameObjects.GameObjectInstanceEditAction;
import gameObjects.GameObjectInstanceEditAction;
import gameObjects.instance.GameInstance;
import gameObjects.instance.ObjectInstance;
import main.Player;

public class GamePanel extends JPanel implements MouseListener, MouseMotionListener, GameInstance.GameChangeListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3579141032474558913L;
	GameInstance gameInstance;
	ObjectInstance activeObject = null;
	int pressedXPos = -1;
	int pressedYPos = -1;
	int clickedXPos = -1;
	int clickedYPos = -1;
	int objOrigPosX = -1;
	int objOrigPosY = -1;
	Player player;
	int id = (int)System.nanoTime();



	public GamePanel(GameInstance gameInstance)
	{
		this.gameInstance = gameInstance;
		addMouseListener(this);
		addMouseMotionListener(this);
		gameInstance.changeListener.add(this);
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		g.drawImage(gameInstance.game.background, 0, 0, getWidth(), getHeight(), Color.BLACK, null);
		for (int i = 0; i < gameInstance.objects.size(); ++i)
		{
			System.out.println(i);
			ObjectInstance oi = gameInstance.objects.get(i);
			double rotationRequired = Math.toRadians (oi.getRotation());
			BufferedImage img = oi.go.getLook(oi.state);
			double locationX = img.getWidth() / 2;
			double locationY = img.getHeight() / 2;
			AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
			g.drawImage(op.filter(img, null), oi.state.posX, oi.state.posY, null);
		}
	}



	@Override
	public void mouseClicked(MouseEvent arg0) {

		/* Right Mouse Click on Object */
		if(SwingUtilities.isRightMouseButton(arg0))
		{
			clickedXPos = arg0.getX();
			clickedYPos = arg0.getY();
			int distance = Integer.MAX_VALUE;
			for (int i = 0;i<gameInstance.objects.size(); ++i)
			{
				ObjectInstance oi = gameInstance.objects.get(i);
				int xDiff = pressedXPos - oi.state.posX, yDiff = pressedYPos - oi.state.posY;
				int dist = xDiff * xDiff + yDiff * yDiff;
				if (dist < distance)
				{
					activeObject = oi;
					distance = dist;
				}
			}
			/*Show popup menu of active object*/
			activeObject.newObjectActionMenu(gameInstance).showPopup(arg0);
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) {
		pressedXPos = arg0.getX();
		pressedYPos = arg0.getY();
		int distance = Integer.MAX_VALUE;
		for (int i = 0;i<gameInstance.objects.size(); ++i)
		{
			ObjectInstance oi = gameInstance.objects.get(i);
			int xDiff = pressedXPos - oi.state.posX, yDiff = pressedYPos - oi.state.posY;
			int dist = xDiff * xDiff + yDiff * yDiff;
			if (dist < distance)
			{
				activeObject = oi;
				distance = dist;
			}
		}
		objOrigPosX = activeObject.state.posX;
		objOrigPosY = activeObject.state.posY;
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		/* Drag only when left mouse down */
		if(SwingUtilities.isLeftMouseButton(arg0)) {
			activeObject.state.posX = objOrigPosX - pressedXPos + arg0.getX();
			activeObject.state.posY = objOrigPosX - pressedXPos + arg0.getY();
			gameInstance.update(new GameObjectInstanceEditAction(id, player, activeObject));
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {}




	@Override
	public void changeUpdate(GameAction action) {
		if (action instanceof GameObjectInstanceEditAction)
		{
			repaint();
		}
	}
}
