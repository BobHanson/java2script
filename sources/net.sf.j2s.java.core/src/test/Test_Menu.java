package test;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import swingjs.JSUtil;

public class Test_Menu extends JFrame {
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		JFrame jf = new Test_Menu();
		if (/** @j2sNative true || */false) {
			JSUtil.isolateWindowGesturesStatic(jf);
		}
	}

	private static class StickyMenuItem extends JMenuItem {

		public StickyMenuItem(String label) {
			super(label);
			putClientProperty("_j2sStickyMenuItem", Boolean.TRUE);
		}

		@Override
		protected void processMouseEvent(MouseEvent e) {
			// Intercept the mouse release event, which triggers the menu closure
			if (e.getID() == MouseEvent.MOUSE_RELEASED && contains(e.getPoint())) {
				// Trigger the action listeners manually
				doClick();
				// Prevent the parent MenuSelectionManager from clearing the menu selection path
				return;
			}
			super.processMouseEvent(e);
		}		
	}

	protected boolean done;
	private JPopupMenu pmenu;

	public Test_Menu() {

			setJMenuBar(getMenuBar());
			JPanel p = new JPanel();
			p.setPreferredSize(new Dimension(300,400));
			add(p);
			addPopupMenu(this);
			pack();
			setVisible(true);
		}


	private void addPopupMenu(JFrame frame) {
		addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent e) {
				System.out.println("----pressed---" + e);
				if (e.isPopupTrigger()) {
					// macs here
					pmenu.show(frame, e.getX(), e.getY());
				}
			}

			public void mouseReleased(MouseEvent e) {
				System.out.println("----released---" + e);
				if (e.isPopupTrigger()) {
					// windows here
					pmenu.show(frame, e.getX(), e.getY());
				}
			}

		});

		pmenu = new JPopupMenu();
		JMenuItem b = new JMenuItem("testing1");
		ActionListener listener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("--clicked--" + e.getSource());
			}

		};
		b.addActionListener(listener);
		pmenu.add(b);
		b = new JMenuItem("testing2");
		b.addActionListener(listener);
		pmenu.add(b);
		b = new JMenuItem("testing3");
		b.addActionListener(listener);
		pmenu.add(b);
	}


	private JMenuBar getMenuBar() {
		JMenuBar mb = new JMenuBar();
		JMenu mb1 = new JMenu("Test1");
		JMenu mb2 = new JMenu("Testing2");
		JMenuItem mb2a = new StickyMenuItem("sticky-2a");
		mb2a.addActionListener((e)->{
			System.err.println("---------sticky-2a clicked");			
		});
		mb2.add(mb2a);
		JMenuItem mb2b = new StickyMenuItem("sticky-2b");
		mb2b.addActionListener((e)->{
			System.err.println("---------sticky-2b clicked");			
		});
		mb2.add(mb2b);
		
		JMenuItem mb1a = new JMenuItem("test-1");
		mb1a.addActionListener((e)->{
			System.out.println("test-1 clicked");
			mb2.removeAll();
			mb1.add(mb1a);
			mb2.add(mb2a);
			System.out.println("test-1 removed and re-added");
			done = true;
		});
		
		mb1.setMnemonic('t');
		mb1a.setMnemonic('1');

		mb1.addMenuListener(new MenuListener() {

			@Override
			public void menuSelected(MenuEvent e) {
				mb1.add(mb1a);
				// TODO Auto-generated method stub
				
			}

			@Override
			public void menuDeselected(MenuEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void menuCanceled(MenuEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});


		mb.add(mb1);
		mb.add(mb2);
		JLabel l = new JLabel("label1");
		mb.add(l);
		l = new JLabel("label2");
		mb1.add(l);

		mb2.add(mb1a);

		return mb;
	}

}
