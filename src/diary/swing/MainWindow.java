package diary.swing;

import java.awt.*;
import java.io.File;

import javax.swing.*;

import diary.files.FileWorker;

public class MainWindow extends JFrame {
	private JFileChooser fileChooser = new JFileChooser();
	private JMenuBar menu = new JMenuBar();
	DefaultListModel<String> model;
	private JList<String> pageList;
	private JTextArea diaryPageArea = new JTextArea(5, 30);
	private JButton buttonAdd = new JButton("Добавить запись");
	private JButton buttonCheck = new JButton("Проверить целостность");
	private Toolkit toolkit = Toolkit.getDefaultToolkit();
	private JPanel panelWest = new JPanel();
	private JPanel panelSouth = new JPanel();
	private JPanel panelCenter = new JPanel();

	FileWorker fileWorker;

	public MainWindow() {
		super("Дневник блокчейн");
		configMenu();
		configPanel();
		configWindow();
		initListeners();
	}

	private void initListeners() {
		buttonAdd.addActionListener(e -> ifButtonAddBlockPressed());
		buttonCheck.addActionListener(e -> ifButtonCheckPressed());
		pageList.addListSelectionListener(e -> ifListValueChanged());
	}

	private void ifButtonAddBlockPressed() {
		String text = diaryPageArea.getText();
		fileWorker.createBlock(text);
		refreshPageList();
	}

	private void ifButtonCheckPressed() {
		JOptionPane.showMessageDialog(this, fileWorker.checkChain());
		refreshPageList();
	}

	private void ifListValueChanged() {
		int index = pageList.getSelectedIndex();
		String textOfBlock = fileWorker.getBlockTextByIndex(index);
		diaryPageArea.setText(textOfBlock);
	}

	private void configWindow() {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		Dimension dimension = toolkit.getScreenSize();
		this.setBounds(dimension.width / 4, dimension.height / 4, dimension.width / 2, dimension.height / 2);
		this.setVisible(true);
	}

	private void configMenu() {
		JMenu file = new JMenu("Файл");
		menu.add(file);
		JMenuItem open = new JMenuItem("Выбрать папку");
		file.add(open);
		open.addActionListener(e -> ifMenuOpenPressed());
		JMenuItem help = new JMenu("Справка");
		menu.add(help);
		JMenuItem about = new JMenuItem("О программе");
		JMenuItem instruction = new JMenuItem("Инструкция");
		help.add(about);
		help.add(instruction);
		instruction.addActionListener(e -> ifMenuInstructionPressed());
		about.addActionListener(e -> ifMenuAboutPressed());

		this.setJMenuBar(menu);
	}

	private void ifMenuAboutPressed() {
		JOptionPane.showMessageDialog(this,
				"Дневник блокчейн\n" + "Программа предназначена для возвращения цифровому дневнику магии неизменности\n"
						+ "и воплощения в цифровом виде поговорки \"Что написано пером, того не вырубишь топором\"");
	}

	private void ifMenuInstructionPressed() {
		JOptionPane.showMessageDialog(this,
				"Для того что бы начать использовать программу необходимо выбрать рабочую папку\n"
						+ "Нажмите в верхнем меню кнопку файл -> выбрать папку\n"
						+ "\nПосле выбора рабочей папки, где будет храниться или уже храниться дневник,\n программа автоматически проверит его целостность"
						+ "и сформирует список доступных страниц для просмотра.\n"
						+ "\nЧто бы добавить новую запись введите новый текст в поле для текста и нажмите кнопку \""
						+ buttonAdd.getText() + "\"" + "\n Для проверки цепи на наличие повреждений нажмите \""
						+ buttonCheck.getText() + "\"");
	}

	private void ifMenuOpenPressed() {
		fileChooser.showOpenDialog(menu);
		File file = fileChooser.getSelectedFile();
		if (file != null) {
			fileWorker = new FileWorker(file);
			buttonAdd.setEnabled(true);
			buttonCheck.setEnabled(true);
			diaryPageArea.setEnabled(true);
			pageList.setEnabled(true);
			refreshPageList();
		}

	}

	private void configPanel() {

		fileChooser.setDialogTitle("Select directory with your diary pages");
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		this.setLayout(new BorderLayout());
		model = new DefaultListModel<String>();
		model.addElement("Список страниц");
		model.addElement(" ");
		model.addElement(" ");
		pageList = new JList<String>(model);
		pageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		diaryPageArea.setText("Текст страницы");
		pageList.setEnabled(false);
		panelWest.add(pageList);
		diaryPageArea.setEnabled(false);
		panelCenter.add(diaryPageArea);

		buttonAdd.setEnabled(false);
		buttonCheck.setEnabled(false);
		panelSouth.add(buttonAdd);
		panelSouth.add(buttonCheck);
		this.add(panelWest, BorderLayout.WEST);
		this.add(panelSouth, BorderLayout.SOUTH);
		this.add(panelCenter, BorderLayout.CENTER);
	}

	private void refreshPageList() {
		model.clear();
		String[] listOfBlocks = fileWorker.getListOfBlocks();
		for (String str : listOfBlocks) {
			model.addElement(str);
		}
	}
}
