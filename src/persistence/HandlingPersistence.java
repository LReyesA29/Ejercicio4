package persistence;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.NodeList;
import org.w3c.dom.Document;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import Model.DataFile;
import Model.Rule;
import Model.Transaction;
import config.Config;
import constants.CommonConstants;
import enums.ETypeFile;
import enums.ETypePayment;
import interfaces.IActionsFile;
import config.*;

public class HandlingPersistence extends FilePlain implements IActionsFile {

	private List<Transaction> listTransactions;
	private List<Transaction> listFilterTransactions;
	private List<Transaction> testList;
	private List<DataFile> listFiles;
	private List<Rule> listRules;

	private ETypeFile file;

	public HandlingPersistence() {
		this.listTransactions = new ArrayList<>();
		this.listFiles = new ArrayList<>();
		this.listRules = new ArrayList<>();
		this.testList = new ArrayList<>();

		this.loadFile(ETypeFile.PRMTRS);

	}

	@Override
	public void loadFile(ETypeFile eTypeFile) {
		if (eTypeFile.equals(ETypeFile.TXT)) {
			String nameFile = config.getNameFileTXT();
			loadFilePlain(nameFile);
		}
		if (eTypeFile.equals(ETypeFile.JSON)) {
			loadFileJSON();
		}
		if (eTypeFile.equals(ETypeFile.CSV)) {
			String nameFile = config.getNameFileCSV();
			loadFilePlain(nameFile);
		}
		if (eTypeFile.equals(ETypeFile.PRMTRS)) {
			loadFileParametrosXML();
		}
	}

	@Override
	public void dumpFile(ETypeFile eTypeFile) {

		if (eTypeFile.equals(ETypeFile.TXT)) {
			String nameFile = config.getNameFileTXT();
			dumpFilePlain(nameFile);
		}
		if (eTypeFile.equals(ETypeFile.JSON)) {
			dumpFileJSON();
		}
		if (eTypeFile.equals(ETypeFile.CSV)) {
			String nameFile = config.getNameFileCSV();
			dumpFilePlain(nameFile);
		}
	}

	private void loadFileParametrosXML() {
		try {
			File file = new File(config.getPathFiles().concat(config.getNameFileParametros()));
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(file);
			// datos
			NodeList list = document.getElementsByTagName(CommonConstants.NAME_TAG_TRANSACTION);
			for (int i = 0; i < list.getLength(); i++) {

				int id = Integer.parseInt(document.getElementsByTagName("transactionId").item(i).getTextContent());
				int code = Integer.parseInt(document.getElementsByTagName("clientId").item(i).getTextContent());
				double amount = Double.parseDouble(document.getElementsByTagName("amount").item(i).getTextContent());
				String date = document.getElementsByTagName("date").item(i).getTextContent();
				String paymentMethod = document.getElementsByTagName("paymentMethod").item(i).getTextContent();

				if (paymentMethod.equals("EFECTIVO")) {
					this.listTransactions.add(new Transaction(id, code, amount, date, ETypePayment.EFECTIVO));
				}
				if (paymentMethod.equals("TARJETA_CREDITO")) {
					this.listTransactions.add(new Transaction(id, code, amount, date, ETypePayment.TARJETA_CREDITO));
				}
				if (paymentMethod.equals("TARJETA_DEBITO")) {
					this.listTransactions.add(new Transaction(id, code, amount, date, ETypePayment.TARJETA_DEBITO));

				}
			}
			// archivo Export
			NodeList list2 = document.getElementsByTagName(CommonConstants.FILES);
			for (int i = 0; i < list2.getLength(); i++) {
				String nameEntity = document.getElementsByTagName("nameEntity").item(i).getTextContent();
				String typefile = document.getElementsByTagName("typeFile").item(i).getTextContent();
				if (typefile.equalsIgnoreCase("CSV")) {
					this.listFiles.add(new DataFile(nameEntity, ETypeFile.CSV));

				}
				if (typefile.equalsIgnoreCase("JSON")) {
					this.listFiles.add(new DataFile(nameEntity, ETypeFile.JSON));

				}
				if (typefile.equalsIgnoreCase("TXT")) {
					this.listFiles.add(new DataFile(nameEntity, ETypeFile.TXT));

				}
			}
			// archivo Rules
			NodeList list3 = document.getElementsByTagName(CommonConstants.RULES);
			for (int i = 0; i < list3.getLength(); i++) {
				String ruleName = document.getElementsByTagName("ruleName").item(i).getTextContent();
				String valueRule = document.getElementsByTagName("valueRule").item(i).getTextContent();

				this.listRules.add(new Rule(ruleName, valueRule));
			}

		} catch (Exception e) {
			System.out.println("Se presentó un error en el cargue del archivo XML");
			e.printStackTrace();
		}
	}

	private void dumpFilePlain(String nameFile) {
		StringBuilder rutaArchivo = new StringBuilder();
		rutaArchivo.append(config.getPathFiles());
		rutaArchivo.append(nameFile);
		List<String> records = new ArrayList<>();
		for (Transaction u : this.listFilterTransactions) {
			StringBuilder contentStudent = new StringBuilder();
			contentStudent.append(u.getId()).append(CommonConstants.SEMICOLON);
			contentStudent.append(u.getClientId()).append(CommonConstants.SEMICOLON);
			contentStudent.append(u.getAmount()).append(CommonConstants.SEMICOLON);
			contentStudent.append(u.getDate()).append(CommonConstants.SEMICOLON);
			contentStudent.append(u.getPaymentMethod());
			records.add(contentStudent.toString());
		}
		this.writer(rutaArchivo.toString(), records);
	}

	private void dumpFileJSON() {
		String path = config.getPathFiles().concat(config.getNameFileJson());
		try (Writer writer = new FileWriter(path)) {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			gson.toJson(listFilterTransactions, writer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadFilePlain(String nameFile) {
		List<String> contentInLine = this.reader(
				config.getPathFiles().concat(nameFile));
		contentInLine.forEach(row -> {
			StringTokenizer tokens = new StringTokenizer(
					row, CommonConstants.SEMICOLON);
			while (tokens.hasMoreElements()) {
				int id = Integer.parseInt(tokens.nextToken());
				int clientID = Integer.parseInt(tokens.nextToken());
				double amount = Double.parseDouble(tokens.nextToken());
				String date = tokens.nextToken();
				String typePayment = tokens.nextToken();

				if (typePayment.equals("EFECTIVO")) {
					this.testList.add(new Transaction(id, clientID, amount, date, ETypePayment.EFECTIVO));
				}
				if (typePayment.equals("TARJETA_CREDITO")) {
					this.testList.add(new Transaction(id, clientID, amount, date, ETypePayment.TARJETA_CREDITO));
				}
				if (typePayment.equals("TARJETA_DEBITO")) {
					this.testList.add(new Transaction(id, clientID, amount, date, ETypePayment.TARJETA_DEBITO));

				}
			}
		});
	}

	private void loadFileJSON() {
		testList.clear();
		String path = config.getPathFiles().concat(config.getNameFileJson());
		File file = new File(path);
		if (!file.exists())
			return;

		try (Reader reader = new FileReader(file)) {
			Gson gson = new Gson();
			List<Transaction> loaded = gson.fromJson(reader, new TypeToken<List<Transaction>>() {
			}.getType());
			if (loaded != null)
				testList.addAll(loaded);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setTestList(List<Transaction> testList) {
		this.testList = testList;
	}

	public List<Transaction> getListTransactions() {
		return listTransactions;
	}

	public List<DataFile> getListFiles() {
		return listFiles;
	}

	public List<Rule> getListRules() {
		return listRules;
	}

	public ETypeFile getFile() {
		return file;
	}

	public List<Transaction> getListFilterTransactions() {
		return listFilterTransactions;
	}

	public void setListFilterTransactions(List<Transaction> listFilterTransactions) {
		this.listFilterTransactions = listFilterTransactions;
	}

	public List<Transaction> getTestList() {
		return testList;
	}

}
