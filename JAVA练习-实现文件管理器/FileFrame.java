package fileManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Objects;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * 
 * @author wei
 *	实现文件管理器窗口
 *	FileFrame 窗口框架
 *	FileListPanel 文件列表面板
 *	PathPanel 路径显示面板和按钮
 *
 */

// 窗口框架
public class FileFrame extends JFrame{
	
	private FileListPanel filelistShow;
	private PathPanel pathPanel;
	String mouseSelectFileName;
	
	public FileFrame()
	{
		//类实例域初始化
		filelistShow=new FileListPanel();
		pathPanel=new PathPanel();
		mouseSelectFileName=new String("");
		
		//设置窗口关闭方法
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//设置窗口位置和大小（由系统平台决定）
		Toolkit kit=Toolkit.getDefaultToolkit();  
		Dimension screen=kit.getScreenSize();
		this.setSize(screen.width/2,screen.height/2);
		this.setLocationByPlatform(true);
		
		//实现路径文本框可以直接控制目录跳转
		pathTextToFile();
		
		//实现鼠标控制文件显示列表
		mouseControlFilelist();
		
		//控制组件摆放位置
		this.add(filelistShow,BorderLayout.CENTER);
		this.add(pathPanel, BorderLayout.NORTH);
	}
	
	public boolean openFile(String path)
	{
		pathPanel.showpath(path);
		return filelistShow.openFile(path);
	}
	
	private void pathTextToFile()
	{
		pathPanel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String command = e.getActionCommand();
				if(command.equals("进入"))  //按下按钮“进入”
				{
					if(!Objects.equals(filelistShow.getLocalPath(),pathPanel.getPathInput()))
						openFile(pathPanel.getPathInput());
					else
						{
							openFile(pathPanel.getPathInput()+"\\"+mouseSelectFileName);
							mouseSelectFileName="";
						}
						
				}
				if(command.equals("返回"))   //按下按钮”返回“
				{
					String backString=pathBackTo(pathPanel.getPathInput());
					if(!openFile(backString))
					{
						openFile(filelistShow.getLocalPath());
					}
				}
				openFile(pathPanel.getPathInput());  //在文本框中回车
			}
		});
	}
	
	private void mouseControlFilelist()
	{
		
		filelistShow.addJListMouseListener(new MouseAdapter() {
			@Override 
			public void mouseClicked(MouseEvent e)
			{
				if(filelistShow.getItemCanClick())
				{
					if(e.getClickCount()==2)
					{
						openFile(pathPanel.getPathInput()+"\\"+mouseSelectFileName);
						mouseSelectFileName="";
					 }
				}
			}
		});
		
		filelistShow.addJListSelectListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO 自动生成的方法存根
				int i=e.getFirstIndex();
				mouseSelectFileName=filelistShow.getListFileName(i);
				
			}
		});
	}
	
	static public String pathBackTo(String path)  //处理路径字符串，删除最后一个\和之后的字符串
	{
		StringBuffer temp=new StringBuffer(path);
		if(temp.length()!=0)
		{
			int start=temp.lastIndexOf("\\");  //转义字符\\表示\
			if(start!=-1) temp.delete(start, temp.length());
			if(temp.charAt(temp.length()-1)==':')
				temp.append('\\');
		}
		return temp.toString();
	}
}

//文件列表面板
class FileListPanel extends JPanel 
{
	DefaultListModel<String> item;
	JList<?> filelist;
	JScrollPane basepanel;
	String localPath;
	String[] manyFiles={""};
	
	
	public FileListPanel()
	{
		item=new DefaultListModel<>();
		filelist=new JList<String>(item);
		basepanel=new JScrollPane(filelist);
		localPath=new String();
		
		this.setLayout(new BorderLayout());
		this.add(basepanel);
	}
	
	public boolean openFile(String path)
	{
		item.removeAllElements();
		File f=new File(path);
		if(f!=null && f.isDirectory())
		{
			manyFiles=f.list();
			if(manyFiles==null)
			{
				manyFiles=new String[1];
				manyFiles[0]="";
			}
			else{
				for(int i=0;i<manyFiles.length;i++)
				{
					item.addElement(manyFiles[i]);	
				}
			}
			localPath=path;
			return true;
		}
		else
		{
			item.addElement("This is not a Directory");
			return false;
		}
	}
	
	public String getListFileName(int i)
	{
		File f=new File(localPath);
		manyFiles=f.list();
		if(manyFiles!=null && manyFiles.length>i && i>=0)
		     return manyFiles[i];
		else return "";
	}
	
	public String getLocalPath()
	{
		return localPath;
	}
	
	public void addJListMouseListener(MouseListener l)
	{
		filelist.addMouseListener(l);
	}
	
	public void addJListSelectListener(ListSelectionListener l)
	{
		filelist.addListSelectionListener(l);
	}
	
	public boolean getItemCanClick()
	{
		if(item==null) return false;
		return item.size()!=0 && 
				!Objects.equals(item.firstElement(),"This is not a Directory"); 
	}
}

//路径显示面板和按钮
class PathPanel extends JPanel
{
	JTextField pathtext;
	JButton enter;
	JButton back;
	JPanel buttonPanel;
	
	public PathPanel()
	{
		pathtext=new JTextField();
		pathtext.setHorizontalAlignment(JTextField.CENTER);
		enter=new JButton("进入");
		back=new JButton("返回");
		buttonPanel=new JPanel();
		
		buttonPanel.add(enter);
		buttonPanel.add(back);
		
		this.setLayout(new BorderLayout());
		this.add(pathtext,BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.EAST);
	}
	 
	public void showpath(String path)  //显示路径字符串
	{
		pathtext.setText(path);
	}
	
	public String getPathInput()    //获取路径文本框内容
	{
		return pathtext.getText();
	}
	
	
	public void addActionListener(ActionListener a)  //为两个按钮添加ActionListener监听器，使用同一个监听器对象
	{
		enter.addActionListener(a);
		back.addActionListener(a);
		pathtext.addActionListener(a);
	}
	
}




