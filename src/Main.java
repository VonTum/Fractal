import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints.Key;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;


public class Main {
	
	public static JFrame f;
	public static Screen s;
	public static JFrame settings;
	public static boolean isMandelbrot = true;
	public static JRadioButton mandel;
	public static JTextField zfunc;
	public static JTextField inputX;
	public static JTextField inputY;
	public static JTextField inputZoom;
	public static double juliaR = 0.0;
	public static double juliaI = 0.0;
	
	//public static long TORTURETIME;
	public static int MAXN = 100, ZOOMSPEED = 10, scrW = 1000, scrH = 1000;
	public static final double XStart = 0, YStart = 0, ZoomX = 4, ZoomY = 4;
	
	// public static final double X1DEF = -2, Y1DEF = -1.5, X2DEF = 1, Y2DEF = 1.5;
	
	
	// public static double test = 10;
	
	public static void main(String[] args) {
		
		
		
		f = new JFrame("Fractal");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		
		s = new Screen(scrW, scrH);
		s.addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				
				double z = Math.signum(e.getPreciseWheelRotation());
				
				for(int i=0; i < Math.abs(e.getPreciseWheelRotation()); i++){
					Screen.x1 -= z*(Screen.zx*(e.getX()-Screen.w/2)/(ZOOMSPEED*Screen.w));
					Screen.y1 -= z*(Screen.zy*(e.getY()-Screen.h/2)/(ZOOMSPEED*Screen.h));
					
					Screen.zx += z*Screen.zx/ZOOMSPEED;
					Screen.zy += z*Screen.zy/ZOOMSPEED;
				}
				
				
				
				s.repaint();
				
			}
		});
		
		f.addComponentListener(new ComponentListener() {
			@Override public void componentShown(ComponentEvent arg0) {}
			@Override public void componentMoved(ComponentEvent arg0) {}
			@Override public void componentHidden(ComponentEvent arg0) {}
			
			@Override
			public void componentResized(ComponentEvent e) {
				s.setSize(f.getSize());
				
				s.zx = s.zx * s.getWidth() / s.w;
				s.zy = s.zy * s.getHeight() / s.h;
				
				s.w = s.getWidth();
				s.h = s.getHeight();
				
			}
		});
		
		f.addKeyListener(new KeyListener() {
			
			@Override public void keyTyped(KeyEvent arg0) {}
			@Override public void keyReleased(KeyEvent arg0) {}
			@Override public void keyPressed(KeyEvent e) {
				switch(e.getKeyCode()){
				case KeyEvent.VK_1: //Color
					Screen.colors = new Color[] {Color.BLUE, Color.GREEN, Color.YELLOW, Color.ORANGE, Color.RED, Color.PINK, Color.MAGENTA, Color.CYAN};
					break;
				case KeyEvent.VK_2: //Cold
					Screen.colors = new Color[32]; //TODO
					for(int i = 0; i < Screen.colors.length; i++){
						Screen.colors[i] = new Color(i*8%256);
					}
					break;
				case KeyEvent.VK_3: //Sparks
					Screen.colors = new Color[32];
					for(int i = 0; i < Screen.colors.length; i++){
						Screen.colors[i] = new Color(Math.abs(8-i%16)*16,Math.abs(8-i%16)*16,Math.abs(8-(i+8)%16)*16);
					}
					break;
				case KeyEvent.VK_4: //GrayScale
					Screen.colors = new Color[32];
					for(int i = 0; i < Screen.colors.length; i++){
						Screen.colors[i] = new Color(i*8%256, i*8%256, i*8%256);
					}
					break;
				case KeyEvent.VK_5: //Sharp
					Screen.colors = new Color[] {Color.WHITE};
				}
				s.repaint();
				
			}
		});
		
		f.add(s);
		f.pack();
		
		
		
		settings = new JFrame("Settings");
		settings.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		settings.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		ButtonGroup type = new ButtonGroup();
		mandel = new JRadioButton();
		mandel.addActionListener(new Clicked());
		mandel.setSelected(true);
		mandel.setName("MandelButton");
		JRadioButton julia = new JRadioButton();
		julia.addActionListener(new Clicked());
		type.add(mandel);
		type.add(julia);
		
		c.gridx = 0;
		c.gridy = 0;
		settings.add(new JLabel("Mandelbrot"), c);
		c.gridx = 0;
		c.gridy = 1;
		settings.add(new JLabel("Julia"), c);
		c.gridx = 1;
		c.gridy = 0;
		settings.add(mandel, c);
		c.gridx = 1;
		c.gridy = 1;
		settings.add(julia, c);
		
		c.gridx = 2;
		c.gridy = 1;
		settings.add(new JLabel("z = "), c);
		zfunc = new JTextField("0.0+1.0i", 20);
		zfunc.addActionListener(new Clicked());
		c.gridx = 3;
		c.gridy = 1;
		settings.add(zfunc, c);
		
		
		inputX = new JTextField(20);
		inputX.addActionListener(new Clicked());
		inputY = new JTextField(20);
		inputY.addActionListener(new Clicked());
		inputZoom = new JTextField(20);
		inputZoom.addActionListener(new Clicked());
		
		c.ipadx = 5;
		
		c.gridx = 0;
		c.gridy = 3;
		settings.add(new JLabel("X:"), c);
		c.gridx = 1;
		c.gridy = 3;
		settings.add(inputX, c);
		c.gridx = 2;
		c.gridy = 3;
		settings.add(new JLabel("Y:"), c);
		c.gridx = 3;
		c.gridy = 3;
		settings.add(inputY, c);
		c.gridx = 4;
		c.gridy = 3;
		settings.add(new JLabel("Zoom:"), c);
		c.gridx = 5;
		c.gridy = 3;
		settings.add(inputZoom, c);
		
		settings.pack();
		
		
		f.setVisible(true);
		settings.setVisible(true);
		
	}public static class Clicked implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			isMandelbrot = mandel.isSelected();
			
			try{
				String[] parts = zfunc.getText().replace("-","+-").split("(?<=.)\\+");
				System.out.println(parts[0]);
				System.out.println(parts[1]);
				double r = 0.0;
				double i = 0.0;
				if(parts[0].charAt(0) == '+')
					parts[0] = parts[0].substring(1, parts[0].length());
				if(parts[0].charAt(parts[0].length()-1) == 'i'){
					i = Double.parseDouble(parts[0].substring(0, parts[0].length()-1));
				}else{
					r = Double.parseDouble(parts[0]);
				}
				if(parts[1].charAt(parts[1].length()-1) == 'i'){
					i = Double.parseDouble(parts[1].substring(0, parts[1].length()-1));
				}else{
					r = Double.parseDouble(parts[1]);
				}
				juliaR = r;
				juliaI = i;
			}catch(NumberFormatException e){};
			try{
				Screen.x1 = Double.parseDouble(inputX.getText());
				Screen.y1 = Double.parseDouble(inputY.getText());
				Screen.zx = Double.parseDouble(inputZoom.getText());
				Screen.zy = Double.parseDouble(inputZoom.getText())/Screen.w*Screen.h;
			}catch(NumberFormatException e){};
			
			s.repaint();
			
		}
		
	}
	
	
	public static class Screen extends JPanel{
		public static Graphics g;
		
		public static Color[] colors = new Color[] {Color.BLUE, Color.GREEN, Color.YELLOW, Color.ORANGE, Color.RED, Color.PINK, Color.MAGENTA, Color.CYAN};
		// public static double x1 = X1DEF, y1 = Y1DEF, x2 = X2DEF, y2 = Y2DEF;
		
		public static double x1 = XStart, y1 = YStart, zx = ZoomX, zy = ZoomY;
		public static int w, h;
		
		public Screen(int w, int h){
			Screen.w = w;
			Screen.h = h;
			setPreferredSize(new Dimension(w,h));
		}
		@Override
		public void paintComponent(Graphics g){
			zfunc.setText((String.valueOf(juliaR) + "+" + String.valueOf(juliaI) + "i").replace("\\+-", "\\-"));
			inputX.setText(String.valueOf(x1));
			inputY.setText(String.valueOf(y1));
			inputZoom.setText(String.valueOf(zx));
			
			this.g = g;
			
			
			
			
			
			//TORTURETIME = System.currentTimeMillis();/*tests*/
			//for(int eta = 0; eta < 50; eta++)
			
			for(int y = 0; y < h; y++){
				for(int x = 0; x < w; x++){
					int n;
					
					/*Complex c = new Complex((double) x*zx/w+(x1-zx/2), (double) y*zy/h+(y1-zy/2));
					Complex ans = new Complex(0,0);
					for(n = 1; n < MAXN; n++){
						ans = ans.mul(ans).add(c);
						if(ans.r*ans.r + ans.i*ans.i > 4){
							break;
						}
					}*/
					
					/*ALL THE WTFs
					 * double cr = x*zx/w+(x1-zx/2);
					double ci = y*zy/h+(y1-zy/2);
					
					double ansr = 0;
					double ansi = 0;
					
					for(n = 1; n < MAXN; n++){
						ansr = ansr*ansr-ansi*ansi+cr;
						ansi = 2*ansr*ansi+ci;
						if(ansr*ansr+ansi*ansi > 4)
							break;
					}*/
					
					//TIME 20484
					double cr = x*zx/w+(x1-zx/2);
					double ci = y*zy/h+(y1-zy/2);
					
					
					
					
					if(isMandelbrot){
						double ansr = 0;
						double ansi = 0;
						double ansrt = 0;
						
						for(n = 1; n < MAXN; n++){
							ansrt = ansr;
							ansr = ansr*ansr-ansi*ansi+cr;
							ansi = 2*ansrt*ansi+ci;
							if(ansr*ansr+ansi*ansi > 4)
								break;
						}
					}else{
						
						double ansr = cr;
						double ansi = ci;
						double ansrt = 0;
						
						for(n = 1; n < MAXN; n++){
							ansrt = ansr;
							ansr = ansr*ansr-ansi*ansi+juliaR;
							ansi = 2*ansrt*ansi+juliaI;
							if(ansr*ansr+ansi*ansi > 4)
								break;
						}
					}
					
					
					if(n >= MAXN){
						g.setColor(Color.BLACK);
					}else{
						g.setColor(colors[n%colors.length]);
					}
					g.fillRect(x, y, 1, 1);
				}
			}
			//System.out.println(System.currentTimeMillis() - TORTURETIME);
			
		}
	}
	public static class Complex{
		public double r, i;
		public Complex(double real, double imag){
			r = real;
			i = imag;
		}
		public Complex add(Complex c){
			return new Complex(r+c.r, i+c.i);
		}
		public Complex sub(Complex c){
			return new Complex(r-c.r, i-c.i);
		}
		public Complex mul(Complex c){
			return new Complex(r*c.r-i*c.i, r*c.i+i*c.r);
		}
		
	}
}
