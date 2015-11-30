package fileManager;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;

import javax.swing.filechooser.FileSystemView;

/**
 *@author wei
 *main函数，主程序体，实现文件管理器
 * */

public class FileManager {

	public static void main(String[] args) {
		// TODO 自动生成的方法存根
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				// TODO 自动生成的方法存根
				FileFrame frame=new FileFrame();
				
				//设置初始目录为系统默认
				FileSystemView rootview=FileSystemView.getFileSystemView();
				File root=rootview.getDefaultDirectory();
				frame.openFile(root.getPath());
				
				frame.setVisible(true);

			}
		});
		
	}

}
