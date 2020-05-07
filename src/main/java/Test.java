import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class Test extends JFrame {
	public Test() {

		  super();

		  final JLabel label = new JLabel();

		  label.setText("程序启动结束");

		  label.setBounds(90, 10, 90, 20);

		  //label.setOpaque(true);

		  //label.setBackground(Color.green);

		  label.setToolTipText("程序启动结束");

		  setLayout(null);

		  add(label);

		}

		public static void main(String[] args) {

		  // TODO Auto-generated method stub

		  Test MS = new Test();

		  MS.setBounds(200, 100, 300, 200);

		  MS.setVisible(true);

		}
}
