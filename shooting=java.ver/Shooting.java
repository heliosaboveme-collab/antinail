import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Shooting extends JFrame {
    public Shooting() {
	setSize(750, 500);
	setTitle("Java Shooting Game");
	setDefaultCloseOperation(EXIT_ON_CLOSE);
	MyJPanel myJPanel = new MyJPanel();
	Container c = getContentPane();
	c.add(myJPanel);	
	setVisible(true);
    }
    
    public static void main(String[] args) {
	new Shooting();
    }
    public class MyJPanel extends JPanel implements MouseListener, MouseMotionListener, ActionListener {
	// 自機の変数
	int jiki_x;
	Image jiki_ima;
	int jiki_w, jiki_h;
	int jiki_move_flg;
	// 敵機の変数
	int n;
	int[] teki_x, teki_y;
	int[] teki_u;
	int[] teki_alive;
	Image teki_ima;
	int teki_w, teki_h;
	// 自機の弾の変数(配列で用意)
	int m_jiki;
	int[] jiki_tama_x, jiki_tama_y;
	int[] jiki_tama_flg;
	// 敵機の弾の変数
	int m_teki;
	double prob;
	int[][] teki_tama_x, teki_tama_y;
	int[][] teki_tama_flg;
	int[] teki_tama_u;
	
	Timer timer;
	
	public MyJPanel() {
	    // 自機の初期設定
	    ImageIcon icon1 = new ImageIcon("jiki.jpg");
	    jiki_ima = icon1.getImage();
	    jiki_w = jiki_ima.getWidth(this);
	    jiki_h = jiki_ima.getHeight(this);
	    jiki_x = 10;
	    jiki_move_flg = -1;
	    
	    // 敵機の初期設定
	    n = 13;
	    teki_x = new int[n];
	    teki_y = new int[n];
	    teki_u = new int[n];
	    teki_alive = new int[n];
	    ImageIcon icon2 = new ImageIcon("teki.jpg");
	    teki_ima = icon2.getImage();
	    teki_w = teki_ima.getWidth(this);
	    teki_h = teki_ima.getHeight(this);
	    for (int i=0; i<n; i++) {
		teki_x[i] = i * (teki_w) + 100;
		teki_y[i] = (i%2) * teki_h + 10;
		teki_u[i] = 10;
		teki_alive[i] = 1;
	    }
	    
	    // 自機の弾の初期設定(配列で用意)
	    m_jiki=2;
	    jiki_tama_x = new int[m_jiki];
	    jiki_tama_y = new int[m_jiki];
	    jiki_tama_flg = new int[m_jiki];
	    for (int i=0; i<m_jiki; i++) {
	    jiki_tama_x[i] = -100;
	    jiki_tama_y[i] = -100;
	    jiki_tama_flg[i] = 0;
	    }
	    
	    // 敵機の弾の初期設定
	    m_teki=1;
	    prob=0.2;
	    teki_tama_x = new int[n][m_teki];
	    teki_tama_y = new int[n][m_teki];
	    teki_tama_flg = new int[n][m_teki];
	    teki_tama_u = new int[n];
	    for (int i=0; i<n; i++) {
		if (Math.random()<prob) {
		teki_tama_flg[i][0] = 1;
		teki_tama_x[i][0] = teki_x[i] + teki_w/2;
		teki_tama_y[i][0] = teki_y[i] + teki_h;
		}
		else {
		teki_tama_flg[i][0] = 0;
		teki_tama_x[i][0] = -100;
		teki_tama_y[i][0] = -100;
		}
	    }

	    for (int i=0; i<n; i++) {
	    for (int j=1; j<m_teki; j++){
	    teki_tama_flg[i][j] = 0;
	    teki_tama_x[i][j] = -100;
	    teki_tama_y[i][j] = -100;
	    }
	    }

	    for (int i=0; i<n; i++){
	    teki_tama_u[i] = i+1;
	    }	    

	    setResizable(false);
	    setBackground(Color.black);
	    
	    addMouseListener(this);
	    addMouseMotionListener(this);
	    
	    timer = new Timer(100, this);
	    timer.start();
	}
	
	/* MouseListener に関連するメソッド */
	public void mouseClicked(MouseEvent e) {
//	待機中の弾を一つ選んで，その弾を発射
	    for (int i=0; i<m_jiki; i++) {
	    if (jiki_tama_flg[i]==0) {
		jiki_tama_x[i] = jiki_x + jiki_w/2;
		jiki_tama_y[i] = 400;
		jiki_tama_flg[i] = 1;
		break;
	    }
	    }
	}
	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { 
	    jiki_move_flg = e.getX();
	}
	public void mouseEntered(MouseEvent e) { }
	
	/* MouseMotionListener に関連するメソッド */
	public void mouseMoved(MouseEvent e) {
	    if ( Math.abs(jiki_x - e.getX()) < 100) {
		jiki_move_flg = -1;
	    }
	    if (jiki_move_flg==-1) {
		jiki_x = e.getX();
	    }
	    
	    Dimension d = getSize();
	    if (jiki_x>d.width-jiki_w) jiki_x = d.width-jiki_w;
	    
	    repaint();
	}
	public void mouseDragged(MouseEvent e) { }
	
	/* ActionListener に関連するメソッド */
	public void actionPerformed(ActionEvent e) {
	    Dimension d = getSize();
	    if (e.getSource()==timer) {
		// 敵機を水平方向に動かす
		for (int i=0; i<n; i++) {
		    teki_x[i] += teki_u[i];
		    if (teki_x[i]<0 || teki_x[i]>(d.width-teki_w)) teki_u[i] = -teki_u[i];
		}
		
		// 自機の弾を動かす（発射中の弾全てに対し行なう）
		for (int j=0; j<m_jiki; j++) {
		if (jiki_tama_flg[j]==1) {
		    jiki_tama_y[j] -= 10;
		    
		    for (int i=0; i<n; i++) {
			if (teki_alive[i]==1 && 
			    teki_x[i]<jiki_tama_x[j] && teki_x[i]+teki_w>jiki_tama_x[j] &&
			    teki_y[i]<jiki_tama_y[j] && teki_y[i]+teki_h>jiki_tama_y[j]) {
			    
			    teki_alive[i] = 0;
			    jiki_tama_flg[j] = 0;
			    
			}
		    }
		    if (jiki_tama_y[j]<0) jiki_tama_flg[j] = 0;
		}
		}
		
		// 敵機の弾を動かす
		for (int i=0; i<n; i++) {
		for (int j=0; j<m_teki; j++) {
		    if (teki_tama_flg[i][j]==1) {
			teki_tama_y[i][j] += teki_tama_u[i];
			// 敵弾が自機に当たったらゲーム終了
			if (jiki_x<teki_tama_x[i][j] && jiki_x+jiki_w>teki_tama_x[i][j] &&
			    400<teki_tama_y[i][j] && 400+jiki_h>teki_tama_y[i][j]) {
			    System.exit(0);
			}
			
			// 敵弾が画面から出たらつぎの弾を待機中(0)にする
			if (teki_tama_y[i][j]>d.height) {
			    teki_tama_flg[i][j] = 0;
//			    if (teki_alive[i]==1) {   // 弾が待機中(0)で、かつ敵機が生存していたら、つぎの弾を発射する
//				teki_tama_flg[i] = 1;
//				teki_tama_x[i] = teki_x[i]+teki_w/2;
//				teki_tama_y[i] = teki_y[i]+teki_h;
//			    }
			}
		    }
//		    else {
//			if (Math.random()<0.1) teki_tama_flg[i] = 1;
//		    }
		}
			if (Math.random()<prob && teki_alive[i]==1){
			int ic = 0;
			    for (int j=0; j<m_teki; j++) {
			        if(teki_tama_flg[i][j]==0 && ic==0){
			        teki_tama_flg[i][j] = 1;
			        teki_tama_x[i][j] = teki_x[i]+teki_w/2;
			        teki_tama_y[i][j] = teki_y[i]+teki_h;
				ic  = 1;
			        }
			    }
			}
		}
		
		// 全ての敵機を破壊していたらゲーム終了
		int exit_flg = 1;
		for (int i=0; i<n; i++) {
		    if (teki_alive[i]==1) exit_flg = 0;
		} 
		if (exit_flg==1) System.exit(0);
		
		repaint();
	    }
	    
	}
	
	/*　画面描画に関連するメソッド */
	public void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    
	    // 自機の描画
	    g.drawImage(jiki_ima, jiki_x, 400, this);
	    
	    // 敵機の描画
	    for (int i=0; i<n; i++) { 
		if (teki_alive[i]==1) g.drawImage(teki_ima, teki_x[i], teki_y[i], this);
	    }
	    
	    // 自機の弾の描画（発射中の弾全てに行なう）
	    for (int i=0; i<m_jiki; i++) {
	    if (jiki_tama_flg[i]==1) {
		g.setColor(Color.yellow);
		g.fillOval(jiki_tama_x[i], jiki_tama_y[i], 5, 10);
	    }
	    }
	    
	    
	    // 敵機の弾の描画
	    for (int i=0; i<n; i++) {
	    for (int j=0; j<m_teki; j++) {
		g.setColor(Color.white);
		if (teki_tama_flg[i][j]==1) g.fillOval(teki_tama_x[i][j], teki_tama_y[i][j], 5, 5);
	    }
	    }

	}
    }
}
