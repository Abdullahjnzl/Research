package columnToFiles;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.swing.JRadioButton;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.ButtonGroup;
import javax.swing.JProgressBar;
import javax.swing.JLabel;

public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private BufferedReader br;
	private File file;
	private int colNum;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JProgressBar progressBar;
	private JButton startBtn;
	private JButton browseBtn;
	private JRadioButton ps2_radioBtn;
	private JRadioButton ps3_radioBtn;
	private JRadioButton ps5_radioBtn;
	private JTextField colNum_tf;
	private JLabel lblStartingFromZero;
	private final ButtonGroup buttonGroup_1 = new ButtonGroup();
	private JRadioButton tweets_radioBtn;
	private JRadioButton reviews_radioBtn;
	private boolean isTweet = false;

	public class Threader extends SwingWorker<Integer, Integer> {

		@Override
		protected Integer doInBackground() throws Exception {
			List<String> list = new ArrayList<String>();
			isTweet = tweets_radioBtn.isSelected();
			try {
				progressBar.setValue(0);
				br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
				XSSFWorkbook workbook = new XSSFWorkbook(file);
				XSSFSheet sheet = workbook.getSheetAt(0);
				int serialCounter = 0;
				int count = 1;
				int spam = 0;
				int nonRelevantPolarity = 0;
				File outputFolder = null;
				if (ps2_radioBtn.isSelected())
					outputFolder = new File(
							System.getenv("USERPROFILE") + "\\Desktop\\resultsOf" + file.getName() + "_2pointScale");
				else if (ps3_radioBtn.isSelected())
					outputFolder = new File(
							System.getenv("USERPROFILE") + "\\Desktop\\resultsOf" + file.getName() + "_3pointScale");
				else if (ps5_radioBtn.isSelected())
					outputFolder = new File(
							System.getenv("USERPROFILE") + "\\Desktop\\resultsOf" + file.getName() + "_5pointScale");
				if (!outputFolder.exists())
					outputFolder.mkdirs();
				File strongPositive = null, positive, neutral = null, negative, strongNegative = null,
						outputFile = null;

				int lastRowNum = sheet.getPhysicalNumberOfRows();
				progressBar.setMaximum(lastRowNum);
				System.out.println(lastRowNum);
				for (int i = 1; i < lastRowNum; i++) {
					Row row = sheet.getRow(i);

					String text = row.getCell(1).getStringCellValue();
					if (text.equals(""))
						text = "empty text";
					String id;
					if (isTweet)
						id = row.getCell(0).getNumericCellValue()+"";
					else {
						id = serialCounter + "";
						serialCounter++;
					}
					if(isTweet)
					if (!id.equals("")) {
						if (!list.contains(id))
							list.add(id);
						else {
							id = id + "1";
							list.add(id);
						}

					} else {
						break;
					}
					positive = new File(outputFolder.getAbsolutePath() + "\\positive");
					if (!positive.exists())
						positive.mkdirs();
					negative = new File(outputFolder.getAbsolutePath() + "\\negative");
					if (!negative.exists())
						negative.mkdirs();
					if (ps2_radioBtn.isSelected()) {
						Cell cell1 = row.getCell(colNum);
						int polarity1 = 1000;
						if (cell1 == null)
							polarity1 = 0;
						else {
							if (cell1.getCellType() == Cell.CELL_TYPE_STRING) {
								if (cell1.getStringCellValue().equals("spam")) {
									spam++;
									continue;
								}
							} else
								polarity1 = (int) row.getCell(colNum).getNumericCellValue();

						}
						if (polarity1 == 0 || polarity1 == 2 || polarity1 == -2) {
							nonRelevantPolarity++;
							continue;
						}

						switch (polarity1) {
						case -1:
							outputFile = new File(negative.getAbsolutePath() + "\\" + id + ".txt");
							break;
						case 1:
							outputFile = new File(positive.getAbsolutePath() + "\\" + id + ".txt");
							break;

						}
					} else {

						Cell cell = row.getCell(colNum);
						int polarity = 1000;
						if (cell == null)
							polarity = 0;
						else {
							if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
								if (cell.getStringCellValue().equals("spam")) {
									spam++;
									continue;
								}
							} else
								polarity = (int) row.getCell(colNum).getNumericCellValue();

						}

						if (ps3_radioBtn.isSelected()) {
							neutral = new File(outputFolder.getAbsolutePath() + "\\neutral");
							if (!neutral.exists())
								neutral.mkdirs();

							if (polarity == 2 || polarity == -2) {
								nonRelevantPolarity++;
								continue;
							}

							switch (polarity) {

							case -1:
								outputFile = new File(negative.getAbsolutePath() + "\\" + id + ".txt");
								break;
							case 0:
								outputFile = new File(neutral.getAbsolutePath() + "\\" + id + ".txt");
								break;
							case 1:
								outputFile = new File(positive.getAbsolutePath() + "\\" + id + ".txt");
								break;

							}
						}
						if (ps5_radioBtn.isSelected()) {
							neutral = new File(outputFolder.getAbsolutePath() + "\\neutral");
							if (!neutral.exists())
								neutral.mkdirs();
							strongPositive = new File(outputFolder.getAbsolutePath() + "\\strong positive");
							if (!strongPositive.exists())
								strongPositive.mkdirs();
							strongNegative = new File(outputFolder.getAbsolutePath() + "\\strong negative");
							if (!strongNegative.exists())
								strongNegative.mkdirs();

							switch (polarity) {
							case -2:
								outputFile = new File(strongNegative.getAbsolutePath() + "\\" + id + ".txt");
								break;
							case -1:
								outputFile = new File(negative.getAbsolutePath() + "\\" + id + ".txt");
								break;
							case 0:
								outputFile = new File(neutral.getAbsolutePath() + "\\" + id + ".txt");
								break;
							case 1:
								outputFile = new File(positive.getAbsolutePath() + "\\" + id + ".txt");
								break;
							case 2:
								outputFile = new File(strongPositive.getAbsolutePath() + "\\" + id + ".txt");
								break;

							}
						}
					}

					OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8");

					writer.write(text);
					count++;
					writer.flush();
					writer.close();
					progressBar.setValue(i);
				}
				workbook.close();
				br.close();
				progressBar.setValue(lastRowNum);
				JOptionPane.showMessageDialog(null,
						"Files Exported to Desktop!\nCount = " + count + "\nSpam = " + spam
								+ "\nNon Relevant Polarity = " + nonRelevantPolarity,
						"Done Successfully", JOptionPane.INFORMATION_MESSAGE);
			} catch (UnsupportedEncodingException e) {
				JOptionPane.showMessageDialog(null, "Unsupported Encoding \n" + e.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(null, "File Not Foun \n" + e.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}

			startBtn.setEnabled(true);
			browseBtn.setEnabled(true);
			progressBar.setValue(0);
			return null;
		}

	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
					frame.setLocationRelativeTo(null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setTitle("ColToFiles Tool");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 397, 247);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Source File",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel.setBounds(6, 11, 377, 46);
		contentPane.add(panel);
		panel.setLayout(null);

		browseBtn = new JButton("Browse");
		browseBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new File(System.getenv("USERPROFILE") + "\\Desktop\\"));
				FileFilter filter = new FileNameExtensionFilter("xlsx file", "xlsx");
				chooser.setFileFilter(filter);
				int val = chooser.showOpenDialog(MainFrame.this);
				if (val == JFileChooser.APPROVE_OPTION) {
					file = new File(chooser.getSelectedFile().getAbsolutePath());
					textField.setText(file.getAbsolutePath());
				}
			}
		});
		browseBtn.setBounds(277, 16, 89, 23);
		panel.add(browseBtn);

		textField = new JTextField();
		textField.setEditable(false);
		textField.setBounds(10, 17, 257, 20);
		panel.add(textField);
		textField.setColumns(10);

		ps2_radioBtn = new JRadioButton("2 point scale");
		ps2_radioBtn.setSelected(true);
		buttonGroup.add(ps2_radioBtn);
		ps2_radioBtn.setHorizontalAlignment(SwingConstants.CENTER);
		ps2_radioBtn.setFont(new Font("Verdana", Font.BOLD, 11));
		ps2_radioBtn.setBounds(18, 83, 109, 23);
		contentPane.add(ps2_radioBtn);

		ps5_radioBtn = new JRadioButton("5 point scale");
		buttonGroup.add(ps5_radioBtn);
		ps5_radioBtn.setHorizontalAlignment(SwingConstants.CENTER);
		ps5_radioBtn.setFont(new Font("Verdana", Font.BOLD, 11));
		ps5_radioBtn.setBounds(276, 83, 109, 23);
		contentPane.add(ps5_radioBtn);

		startBtn = new JButton("START");
		startBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (textField.getText().equals(""))
					JOptionPane.showMessageDialog(null, "choose the dataset file first.");
				else {
					startBtn.setEnabled(false);
					browseBtn.setEnabled(false);
					String col = colNum_tf.getText().trim();
					if (col != null || !col.equals("")) {
						colNum = Integer.parseInt(col);
						new Threader().execute();
					} else
						JOptionPane.showMessageDialog(null, "Please spcify the column number", "Error",
								JOptionPane.ERROR_MESSAGE);
				}

			}
		});
		startBtn.setBounds(56, 137, 277, 29);
		contentPane.add(startBtn);

		ps3_radioBtn = new JRadioButton("3 point scale");
		buttonGroup.add(ps3_radioBtn);
		ps3_radioBtn.setHorizontalAlignment(SwingConstants.CENTER);
		ps3_radioBtn.setFont(new Font("Verdana", Font.BOLD, 11));
		ps3_radioBtn.setBounds(145, 83, 109, 23);
		contentPane.add(ps3_radioBtn);

		progressBar = new JProgressBar();
		progressBar.setBounds(56, 169, 277, 14);
		contentPane.add(progressBar);

		JLabel lblDevelopedByAbdullah = new JLabel("Developed by: Abdullah Nazzal");
		lblDevelopedByAbdullah.setForeground(Color.GRAY);
		lblDevelopedByAbdullah.setFont(new Font("Verdana", Font.PLAIN, 10));
		lblDevelopedByAbdullah.setBounds(6, 194, 190, 14);
		contentPane.add(lblDevelopedByAbdullah);

		JLabel lblColumnNumber = new JLabel("Column Number : ");
		lblColumnNumber.setBounds(26, 113, 101, 14);
		contentPane.add(lblColumnNumber);

		colNum_tf = new JTextField();
		colNum_tf.setBounds(128, 110, 53, 20);
		contentPane.add(colNum_tf);
		colNum_tf.setColumns(10);

		lblStartingFromZero = new JLabel("Starting from zero");
		lblStartingFromZero.setForeground(Color.GRAY);
		lblStartingFromZero.setBounds(191, 113, 117, 14);
		contentPane.add(lblStartingFromZero);

		tweets_radioBtn = new JRadioButton("Tweets");
		buttonGroup_1.add(tweets_radioBtn);
		tweets_radioBtn.setSelected(true);
		tweets_radioBtn.setBounds(58, 57, 109, 23);
		contentPane.add(tweets_radioBtn);

		reviews_radioBtn = new JRadioButton("Reviews");
		buttonGroup_1.add(reviews_radioBtn);
		reviews_radioBtn.setBounds(199, 57, 109, 23);
		contentPane.add(reviews_radioBtn);
	}

	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public JButton getStartBtn() {
		return startBtn;
	}

	public JButton getBrowseBtn() {
		return browseBtn;
	}

	public JRadioButton getPs2_radioBtn() {
		return ps2_radioBtn;
	}

	public JRadioButton getPs3_radioBtn() {
		return ps3_radioBtn;
	}

	public JRadioButton getPs5_radioBtn() {
		return ps5_radioBtn;
	}

	public JRadioButton getTweets_radioBtn() {
		return tweets_radioBtn;
	}

	public JRadioButton getReviews_radioBtn() {
		return reviews_radioBtn;
	}
}
