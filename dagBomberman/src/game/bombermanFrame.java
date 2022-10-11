package game;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import java.awt.event.KeyEvent;

public class bombermanFrame extends JFrame
{
    private Map Map; 
    private bombermanComponent bombermanComponent;

    public bombermanFrame(final String title, Map Map) throws HeadlessException {
	super(title);
	this.Map = Map;
	this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	bombermanComponent = new bombermanComponent(Map);
	Map.createPlayer(bombermanComponent, Map);
	setKeyStrokes();

	this.setLayout(new BorderLayout());
	this.add(bombermanComponent, BorderLayout.CENTER);
	this.pack();
	this.setVisible(true);
    }

    public bombermanComponent getBombermanComponent() {
	return bombermanComponent;
    }

    private void setKeyStrokes() {

	KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
	bombermanComponent.getInputMap().put(stroke, "q");
	bombermanComponent.getActionMap().put("q", quit);
    }

    private final Action quit = new AbstractAction()
    {
	public void actionPerformed(ActionEvent e) {
		dispose();
	    
	}
    };
}

