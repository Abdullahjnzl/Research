import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.SymbolEntity;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.URLEntity;
import twitter4j.UserMentionEntity;
import twitter4j.auth.AccessToken;

public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JTextField textField_1;
	private static OutputStream OUT = null;
	private static BufferedReader BR = null;
	private static boolean OPTION = false; // single tweet --> false | multi
											// tweets --> ture
	private static String DESTPATH = "";
	private static String TWEET = "";
	private static String CONSUMER_KEY = "4mLdQqsxv6TWWOVouifCdmhEB";
	private static String CONSUMER_SECRET = "5kZZ8SAjsz8L9llYYamUdvXEqOyTCCN5VEyeKVKOSJekWmxYzt";
	private static String ACCESS_TOKEN = "4113051087-7G7yDcsFF4wn5hfly6xnt1ExFcrdO95c2tM7U1X";
	private static String ACCESS_TOKEN_SECRET = "LNrqLcqJEtUN64zeUDQxNVTnGyMPcKnzWHxKuRjlk2yXa";
	private static int ROWCOUNT = 0;
	static int remainingTime = 900;
	static int hashtags;
	private Timer timer;
	private static XSSFWorkbook WORKBOOK = null;
	private static XSSFSheet SHEET = null;
	private JButton browseBtn;
	private JButton getTweetsBtn;
	private JButton rffBtn;
	private JProgressBar progressBar;
	private JButton exitBtn;
	private JRadioButton rdbtnArabic;
	private JRadioButton rdbtnEnglish;
	private JLabel hashtag_lbl;
	private JLabel lblDownloading;
	private JLabel queries_lbl;
	private JLabel label11;
	private JLabel tweets_lbl;
	private JLabel lblQueries;
	private JLabel hashtagsLeft;
	private JTextField since_tf;
	private JTextField until_tf;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public class Threader extends SwingWorker<Integer, Integer> {

		private List<String> tokens = new ArrayList<String>();
		private List<String> texts = new ArrayList<String>();

		@Override
		protected Integer doInBackground() {

			WORKBOOK = new XSSFWorkbook();
			SHEET = WORKBOOK.createSheet("data");
			Row row = SHEET.createRow(ROWCOUNT++);
			Cell cell1, cell2, cell3, cell4, cell10, cell11, cell14;
			cell1 = row.createCell(0);
			cell1.setCellValue("ID");
			cell2 = row.createCell(1);
			cell2.setCellValue("Owner");
			cell3 = row.createCell(2);
			cell3.setCellValue("Retweets");
			cell4 = row.createCell(3);
			cell4.setCellValue("Text");
			cell10 = row.createCell(4);
			cell10.setCellValue("Filtered Text");
			cell11 = row.createCell(5);
			cell11.setCellValue("Date");
			cell14 = row.createCell(6);
			cell14.setCellValue("Country");

			AccessToken accessToken = new AccessToken(ACCESS_TOKEN, ACCESS_TOKEN_SECRET);
			Twitter twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
			twitter.setOAuthAccessToken(accessToken);

			if (OPTION == false) { // single tweet

				int counter = 0;
				Query query = new Query(TWEET);
				query = query.count(100);
				String since = since_tf.getText().toString().trim();
				if (!since.equals("YYYY-MM-DD") && !since.isEmpty()) {
					query.setSince(since);
				}

				String until = until_tf.getText().toString().trim();
				if (!until.equals("YYYY-MM-DD") && !until.isEmpty()) {
					query.setUntil(until);
				}

				QueryResult result;
				try {
					result = twitter.search(query);
					List<Status> tweets = result.getTweets();

					if (tweets.size() >= 100) {
						while ((query = result.nextQuery()) != null) {
							query = query.count(100);
							result = twitter.search(query);
							tweets.addAll(result.getTweets());
						}
					}

					progressBar.setMaximum(tweets.size());
					progressBar.setValue(1);
					for (Status tweet : tweets) {

						if (!found(tweet)) {

							Row row1 = SHEET.createRow(ROWCOUNT++);
							Cell cell5, cell6, cell7, cell8, cell9, cell13, cell15;
							cell5 = row1.createCell(0);
							CellStyle style = WORKBOOK.createCellStyle();
							style.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
							cell5.setCellStyle(style);
							cell5.setCellValue(tweet.getId());
							cell6 = row1.createCell(1);
							cell6.setCellValue("@" + tweet.getUser().getScreenName());
							cell7 = row1.createCell(2);
							cell7.setCellValue(tweet.getRetweetCount());
							cell8 = row1.createCell(3);
							cell8.setCellValue(tweet.getText());
							cell9 = row1.createCell(4);
							cell9.setCellValue(texts.get(texts.size() - 1));
							cell13 = row1.createCell(5);
							cell13.setCellValue(tweet.getCreatedAt().toString());
							cell15 = row1.createCell(6);
							if (tweet.getPlace() != null) {
								cell15.setCellValue(tweet.getPlace().getCountry().toString());
							} else
								cell15.setCellValue("null");
							progressBar.setValue(++counter);
						} else {
							progressBar.setValue(++counter);
							continue;
						}

					}
					tweets_lbl.setText(tweets.size() + "");

				} catch (TwitterException e) {
					JOptionPane.showMessageDialog(null, "Error in query,\n" + e.getMessage(), "Error",
							JOptionPane.ERROR_MESSAGE);
				}

				try {
					SHEET.autoSizeColumn(0);
					SHEET.autoSizeColumn(1);
					SHEET.autoSizeColumn(2);
					SHEET.autoSizeColumn(3);
					SHEET.autoSizeColumn(4);
					SHEET.autoSizeColumn(5);
					SHEET.autoSizeColumn(6);
					WORKBOOK.write(OUT);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "can't write to file " + DESTPATH + "\n" + e.getMessage(),
							"Error", JOptionPane.ERROR_MESSAGE);
				}

				try {
					OUT.close();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "can't close the file" + "\n" + e.getMessage(), "Error",
							JOptionPane.ERROR_MESSAGE);
				}
				ROWCOUNT = 0;
				texts = new ArrayList<String>();
			}else if (OPTION == true) { // multi tweets
				progressBar.setValue(0);
				String line = "";
				int counter = 0;
				try {
					while ((line = BR.readLine()) != null) {
						if (!line.trim().equals(""))
							tokens.add(line.trim());

					}

					hashtags = tokens.size();
					if (!tokens.isEmpty()) {
						progressBar.setMaximum(tokens.size());
						boolean exit = false;
						int queries = 0;
						for (int i = 0; i < tokens.size() && !exit; i++) {
							String item = tokens.get(i);
							if (item.trim().equals("")) {
								progressBar.setValue(++counter);
								continue;
							}

							Query query = new Query(item);
							String since = since_tf.getText().toString().trim();
							if (!since.equals("YYYY-MM-DD") && !since.isEmpty()) {
								query.setSince(since);
							}
							String until = until_tf.getText().toString().trim();
							if (!until.equals("YYYY-MM-DD") && !until.isEmpty()) {
								query.setUntil(until);
							}
							query = query.count(200);

							QueryResult result;

							List<Status> tweets = new ArrayList<Status>();

							try {
								result = twitter.search(query);
								tweets = result.getTweets();

								queries++;
								if (tweets.size() >= 100) {
									while ((query = result.nextQuery()) != null) {
										query = query.count(100);
										result = twitter.search(query);
										queries++;
										tweets.addAll(result.getTweets());
									}
								}
								hashtag_lbl.setText(item);
								tweets_lbl.setText(tweets.size() + "");
								queries_lbl.setText(queries + "");
								hashtagsLeft.setText("Hasttags Left: " + hashtags);
								hashtags--;
								for (Status tweet : tweets) {

									if (!found(tweet)) {
										Row row1 = SHEET.createRow(ROWCOUNT++);
										Cell cell5, cell6, cell7, cell8, cell9, cell12, cell16;
										cell5 = row1.createCell(0);
										CellStyle style = WORKBOOK.createCellStyle();
										style.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
										cell5.setCellStyle(style);
										cell5.setCellValue(tweet.getId());
										cell6 = row1.createCell(1);
										cell6.setCellValue("@" + tweet.getUser().getScreenName());
										cell7 = row1.createCell(2);
										cell7.setCellValue(tweet.getRetweetCount());
										cell8 = row1.createCell(3);
										cell8.setCellValue(tweet.getText());
										cell9 = row1.createCell(4);
										cell9.setCellValue(texts.get(texts.size() - 1));
										cell12 = row1.createCell(5);
										cell12.setCellValue(tweet.getCreatedAt());
										cell16 = row1.createCell(6);
										if (tweet.getPlace() != null) {
											cell16.setCellValue(tweet.getPlace().getCountry().toString());
										} else
											cell16.setCellValue("null");
									} else {
										continue;
									}

								}

							} catch (TwitterException e) {

								int val = JOptionPane.showConfirmDialog(null,
										"You have exceeded the quiries limit, you need to wait 15 minutes.\nSleep for 15 minutes?",
										"Note", JOptionPane.YES_NO_OPTION);
								if (val == JOptionPane.YES_OPTION) {
									pause();
								} else {
									exit = true;
									break;
								}

							}

							progressBar.setValue(++counter);
						}
					}
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "Can't read from file " + e.getMessage(), "Error",
							JOptionPane.ERROR_MESSAGE);
				}
				try {
					SHEET.autoSizeColumn(0);
					SHEET.autoSizeColumn(1);
					SHEET.autoSizeColumn(2);
					SHEET.autoSizeColumn(3);
					SHEET.autoSizeColumn(4);
					SHEET.autoSizeColumn(5);
					SHEET.autoSizeColumn(6);
					WORKBOOK.write(OUT);

				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "can't write to file " + DESTPATH + "\n" + e.getMessage(),
							"Error", JOptionPane.ERROR_MESSAGE);
				}
				try {
					BR.close();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "can't close the reader" + "\n" + e.getMessage(), "Error",
							JOptionPane.ERROR_MESSAGE);
				}

			}

			try {
				OUT.close();

			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "can't close the file" + "\n" + e.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}

			JOptionPane.showMessageDialog(null, "Downloading tweets done successfully", "Done",
					JOptionPane.INFORMATION_MESSAGE);
			normal();
			ROWCOUNT = 0;
			tokens = new ArrayList<String>();
			texts = new ArrayList<String>();
			return null;
		}

		protected boolean found(Status tweet) {

			String text = tweet.getText();
			URLEntity[] urls = tweet.getURLEntities();
			HashtagEntity[] tags = tweet.getHashtagEntities();
			UserMentionEntity[] users = tweet.getUserMentionEntities();
			SymbolEntity[] symbols = tweet.getSymbolEntities();
			MediaEntity[] medias = tweet.getMediaEntities();

			for (HashtagEntity tag : tags) {
				text = text.replace("#" + tag.getText(), "");

			}

			for (UserMentionEntity user : users) {
				text = text.replace("@" + user.getScreenName(), "");

			}

			for (SymbolEntity symbol : symbols) {
				text = text.replace(symbol.getText(), "");

			}

			for (URLEntity url : urls) {
				text = text.replace(url.getText(), "");

			}

			for (MediaEntity media : medias) {
				text = text.replace(media.getText(), "");

			}

			if (text.startsWith("RT :"))
				text = text.replace("RT :", "");
			if (text.startsWith("New post:"))
				text = text.replace("New post:", "");
			text = text.trim();
			if (rdbtnArabic.isSelected()) {
				Pattern pattern = Pattern.compile("[\" [^\\p{InArabic}]+]");
				Matcher matcher = pattern.matcher(text);
				text = matcher.replaceAll(" ");
			}
			if(rdbtnEnglish.isSelected()){
				Pattern pattern = Pattern.compile("\\W+");
				Matcher matcher = pattern.matcher(text);
				text = matcher.replaceAll(" ");
			}

			text = text.trim();
			if (!texts.contains(text)) {
				texts.add(text);
				return false;
			} else
				return true;

		}


		protected void pause() {
			// try {
			hashtag_lbl.setText("");
			tweets_lbl.setText("");
			lblDownloading.setVisible(false);
			lblQueries.setVisible(false);
			label11.setVisible(false);
			queries_lbl.setText("");
			hashtagsLeft.setText("");

			timer = new Timer(1000, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					LocalTime time = LocalTime.ofSecondOfDay(remainingTime);
					if (--remainingTime > 0)
						hashtag_lbl.setText(time.toString());
					else
						timer.stop();
				}
			});
			timer.start();
			try {
				Thread.sleep(60000 * 15);

				lblDownloading.setVisible(true);
				lblQueries.setVisible(true);
				label11.setVisible(true);

			} catch (InterruptedException e1) {

				e1.printStackTrace();
			}
		}

	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setResizable(false);
		setTitle("Twitter Crawler v2.0");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 497, 302);
		Image image = new ImageIcon(getClass().getResource("tweet-icon.png")).getImage();
		setIconImage(image);
		contentPane = new JPanel();
		contentPane.setBorder(new LineBorder(Color.GRAY));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblDevelopedBy = new JLabel("Developed By : Abdullah Nazzal");
		lblDevelopedBy.setForeground(Color.GRAY);
		lblDevelopedBy.setFont(new Font("Verdana", Font.PLAIN, 11));
		lblDevelopedBy.setBounds(298, 249, 193, 14);
		contentPane.add(lblDevelopedBy);

		JLabel lblTwitterCrawlerV = new JLabel("Twitter Crawler v2.0");
		lblTwitterCrawlerV.setForeground(Color.GRAY);
		lblTwitterCrawlerV.setFont(new Font("Verdana", Font.PLAIN, 11));
		lblTwitterCrawlerV.setBounds(18, 249, 133, 14);
		contentPane.add(lblTwitterCrawlerV);

		textField = new JTextField();
		textField.setText("#");
		textField.setHorizontalAlignment(JTextField.RIGHT);
		textField.setFont(new Font("Arial", Font.PLAIN, 15));
		textField.setCaretPosition(0);
		textField.setBounds(10, 77, 339, 33);
		contentPane.add(textField);
		textField.setColumns(10);

		getTweetsBtn = new JButton("Get Tweets");
		getTweetsBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File file = new File(DESTPATH);
				try {
					OUT = new FileOutputStream(file);
					if (textField.getText().trim().equals("#") || textField.getText().trim().equals("")) {
						textField.requestFocus();
						JOptionPane.showMessageDialog(null, "Please Enter the tweet first", "Error",
								JOptionPane.ERROR_MESSAGE);
					} else {
						TWEET = textField.getText().trim();
						OPTION = false;
						new Threader().execute();
						busy();
					}
				} catch (FileNotFoundException e) {
					JOptionPane.showMessageDialog(null, "Destination File Not Found,\n" + e.getMessage(), "Error",
							JOptionPane.ERROR_MESSAGE);
				}

			}
		});
		getTweetsBtn.setBounds(358, 77, 119, 33);
		contentPane.add(getTweetsBtn);

		rdbtnEnglish = new JRadioButton("English");
		rdbtnEnglish.setFont(new Font("Verdana", Font.PLAIN, 11));
		rdbtnEnglish.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				textField.setHorizontalAlignment(JTextField.LEFT);
				textField.setFont(new Font("Verdana", Font.PLAIN, 15));
				textField.requestFocus();
				textField.setCaretPosition(textField.getText().length());
			}
		});
		buttonGroup.add(rdbtnEnglish);
		rdbtnEnglish.setBounds(10, 117, 73, 23);
		contentPane.add(rdbtnEnglish);

		rdbtnArabic = new JRadioButton("Arabic");
		rdbtnArabic.setFont(new Font("Verdana", Font.PLAIN, 11));
		rdbtnArabic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				textField.setHorizontalAlignment(JTextField.RIGHT);
				textField.setFont(new Font("Arial", Font.PLAIN, 15));
				textField.requestFocus();
				textField.setCaretPosition(0);
			}
		});
		buttonGroup.add(rdbtnArabic);
		rdbtnArabic.setSelected(true);
		rdbtnArabic.setBounds(85, 117, 66, 23);
		contentPane.add(rdbtnArabic);

		rffBtn = new JButton("Read From File");
		rffBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File file = new File(DESTPATH);

				try {
					OUT = new FileOutputStream(file);
					JFileChooser chooser = new JFileChooser();
					FileFilter filter = new FileNameExtensionFilter(".txt", "txt");
					chooser.setFileFilter(filter);
					chooser.setCurrentDirectory(new File(System.getenv("USERPROFILE") + "\\Desktop"));
					int val = chooser.showOpenDialog(MainFrame.this);
					if (val == JFileChooser.APPROVE_OPTION) {
						try {
							BR = new BufferedReader(
									new InputStreamReader(new FileInputStream(chooser.getSelectedFile()), "UTF-8"));
							OPTION = true;
							new Threader().execute();
							busy();

						} catch (UnsupportedEncodingException e) {
							JOptionPane.showMessageDialog(null, "Unsupported endcoding " + e.getMessage(), "Error",
									JOptionPane.ERROR_MESSAGE);
						} catch (FileNotFoundException e) {
							JOptionPane.showMessageDialog(null, "FileNotFound " + e.getMessage(), "Error",
									JOptionPane.ERROR_MESSAGE);
						}
					}
				} catch (FileNotFoundException e1) {
					JOptionPane.showMessageDialog(null, "Destination File Not Found,\n" + e1.getMessage(), "Error",
							JOptionPane.ERROR_MESSAGE);
				}

			}
		});
		rffBtn.setBounds(358, 123, 119, 33);
		contentPane.add(rffBtn);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Destination Folder",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel.setBounds(4, 14, 473, 56);
		contentPane.add(panel);
		panel.setLayout(null);

		textField_1 = new JTextField();
		textField_1.setBounds(6, 17, 339, 28);
		panel.add(textField_1);
		textField_1.setColumns(10);

		browseBtn = new JButton("Browse");
		browseBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				FileFilter filter = new FileNameExtensionFilter("xlsx file", "xlsx");
				chooser.setFileFilter(filter);
				chooser.setCurrentDirectory(new File(System.getenv("USERPROFILE") + "\\Desktop\\"));
				int val = chooser.showSaveDialog(MainFrame.this);
				if (val == JFileChooser.APPROVE_OPTION) {
					String path = chooser.getSelectedFile().getAbsolutePath();
					if (!path.endsWith(".xlsx"))
						path += ".xlsx";
					DESTPATH = path;
					textField_1.setText(DESTPATH);

				}
			}
		});
		browseBtn.setBounds(354, 16, 109, 28);
		panel.add(browseBtn);

		progressBar = new JProgressBar();
		progressBar.setVisible(false);
		progressBar.setForeground(SystemColor.textHighlight);
		progressBar.setBounds(18, 167, 340, 23);
		contentPane.add(progressBar);

		exitBtn = new JButton("Exit");
		exitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		exitBtn.setBounds(358, 167, 119, 33);
		contentPane.add(exitBtn);

		hashtag_lbl = new JLabel("");
		hashtag_lbl.setBounds(111, 203, 95, 14);
		hashtag_lbl.setHorizontalAlignment(JLabel.RIGHT);
		contentPane.add(hashtag_lbl);

		lblDownloading = new JLabel("Downloading");
		lblDownloading.setVisible(false);
		lblDownloading.setForeground(SystemColor.textHighlight);
		lblDownloading.setBounds(18, 203, 95, 14);
		ImageIcon icon = new ImageIcon(getClass().getResource("loading.gif"));
		lblDownloading.setIcon(icon);
		contentPane.add(lblDownloading);

		lblQueries = new JLabel("Queries: ");
		lblQueries.setVisible(false);
		lblQueries.setBounds(46, 228, 56, 14);
		contentPane.add(lblQueries);

		queries_lbl = new JLabel("");
		queries_lbl.setBounds(98, 228, 46, 14);
		contentPane.add(queries_lbl);

		label11 = new JLabel("Tweets: ");
		label11.setVisible(false);
		label11.setBounds(142, 228, 56, 14);
		contentPane.add(label11);

		tweets_lbl = new JLabel("");
		tweets_lbl.setBounds(196, 228, 46, 14);
		contentPane.add(tweets_lbl);

		hashtagsLeft = new JLabel("");
		hashtagsLeft.setBounds(216, 203, 131, 14);
		contentPane.add(hashtagsLeft);

		JLabel lblSince = new JLabel("Since: ");
		lblSince.setBounds(157, 121, 46, 14);
		contentPane.add(lblSince);

		since_tf = new JTextField();

		since_tf.setBounds(196, 119, 153, 20);
		contentPane.add(since_tf);
		since_tf.setColumns(10);
		since_tf.setText("YYYY-MM-DD");
		since_tf.setForeground(Color.GRAY);

		JLabel lblUntil = new JLabel("Until: ");
		lblUntil.setBounds(157, 148, 46, 14);
		contentPane.add(lblUntil);

		until_tf = new JTextField();
		until_tf.setText("YYYY-MM-DD");
		until_tf.setForeground(Color.GRAY);
		until_tf.setColumns(10);
		until_tf.setBounds(196, 146, 153, 20);
		contentPane.add(until_tf);

	}

	public void busy() {
		textField.setEnabled(false);
		textField_1.setEnabled(false);
		browseBtn.setEnabled(false);
		getTweetsBtn.setEnabled(false);
		rffBtn.setEnabled(false);
		progressBar.setVisible(true);
		exitBtn.setEnabled(false);
		rdbtnArabic.setEnabled(false);
		rdbtnEnglish.setEnabled(false);
		lblDownloading.setVisible(true);
		lblQueries.setVisible(true);
		queries_lbl.setVisible(true);
		label11.setVisible(true);
		tweets_lbl.setVisible(true);
	}

	public void normal() {
		textField.setEnabled(true);
		textField_1.setEnabled(true);
		browseBtn.setEnabled(true);
		getTweetsBtn.setEnabled(true);
		rffBtn.setEnabled(true);
		progressBar.setVisible(false);
		rdbtnArabic.setEnabled(true);
		rdbtnEnglish.setEnabled(true);
		exitBtn.setEnabled(true);
		lblDownloading.setVisible(false);
		hashtag_lbl.setText("");
		lblQueries.setVisible(false);
		queries_lbl.setText("");
		label11.setVisible(false);
		tweets_lbl.setText("");
		hashtagsLeft.setText("");
	}

	public JButton getBrowseBtn() {
		return browseBtn;
	}

	public JButton getGetTweetsBtn() {
		return getTweetsBtn;
	}

	public JButton getRffBtn() {
		return rffBtn;
	}

	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public JButton getExitBtn() {
		return exitBtn;
	}

	public JRadioButton getRdbtnArabic() {
		return rdbtnArabic;
	}

	public JRadioButton getRdbtnEnglish() {
		return rdbtnEnglish;
	}

	public JLabel getLabel2() {
		return hashtag_lbl;
	}

	public JLabel getLblDownloading() {
		return lblDownloading;
	}

	public JLabel getQueries_lbl() {
		return queries_lbl;
	}

	public JLabel getTweets_lbl() {
		return tweets_lbl;
	}

	public JLabel getLblQueries() {
		return lblQueries;
	}

	public JLabel getLabel11() {
		return label11;
	}

	public JLabel getHashtagsLeft() {
		return hashtagsLeft;
	}
}
