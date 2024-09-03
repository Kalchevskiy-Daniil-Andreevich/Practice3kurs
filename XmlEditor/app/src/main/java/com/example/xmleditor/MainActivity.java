package com.example.xmleditor;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class MainActivity extends AppCompatActivity {

    private static final int DELAY_SHORT = 3000;
    private static final int DELAY_LONG = 6000;

    private EditText btnEditText;
    private Button btnOpenFile;
    private Button btnViewFile;
    private Button btnSaveFile;
    private Button btnCloseApp;
    private Button btnTransformXml;
    private Button btnCancelTransform;

    private boolean fileOpened = false;
    private boolean fileChanged = false;
    private boolean isTransformed = false;
    private String savedText = "";
    private String selectedFileName = "";
    private Set<String> transformedFiles = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        btnEditText = findViewById(R.id.editText);
        btnOpenFile = findViewById(R.id.btnOpenFile);
        btnViewFile = findViewById(R.id.btnViewFile);
        btnSaveFile = findViewById(R.id.btnSaveFile);
        btnCloseApp = findViewById(R.id.btnCloseApp);
        btnTransformXml = findViewById(R.id.btnTransformXml);
        btnCancelTransform = findViewById(R.id.btnCancelTransform);

        btnViewFile.setEnabled(false);
        btnOpenFile.setEnabled(true);
        btnTransformXml.setEnabled(false);
        btnCancelTransform.setEnabled(false);
    }

    private void setupListeners() {
        btnOpenFile.setOnClickListener(v -> selectFile());
        btnViewFile.setOnClickListener(v -> viewFile());
        btnSaveFile.setOnClickListener(v -> saveFile(btnEditText.getText().toString()));
        btnTransformXml.setOnClickListener(v -> transformXml());
        btnCloseApp.setOnClickListener(v -> closeApp());
        btnCancelTransform.setOnClickListener(v -> cancelTransform());
        btnEditText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fileChanged = true;
            }
        });
    }

    private void selectFile() {
        int[] rawFiles = {R.raw.library, R.raw.products, R.raw.students};
        String[] fileNames = {"library.xml", "products.xml", "students.xml"};

        new AlertDialog.Builder(this)
                .setTitle("Select XML File")
                .setItems(fileNames, (dialog, which) -> {
                    selectedFileName = fileNames[which];
                    copyFileFromRaw(rawFiles[which], selectedFileName);
                    isTransformed = false;
                    transformedFiles.remove(selectedFileName);
                })
                .show();
    }

    private void copyFileFromRaw(int rawResourceId, String fileName) {
        File file = new File(getFilesDir(), fileName);
        if (!file.exists()) {
            try {
                InputStream inputStream = getResources().openRawResource(rawResourceId);
                FileOutputStream outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                inputStream.close();
                outputStream.close();

                showToastDelayed("File copied to internal storage", DELAY_SHORT);
                btnOpenFile.setEnabled(false);
                readFile();
            } catch (IOException e) {
                e.printStackTrace();
                showToast("Error copying file: " + e.getMessage());
            }
        } else {
            showToastDelayed("File already exists in internal storage", DELAY_SHORT);
            btnOpenFile.setEnabled(false);
            readFile();
        }
        isTransformed = false;
        transformedFiles.remove(fileName);
    }

    private void readFile() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput(selectedFileName)))) {

            StringBuilder stringBuilder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            String fileContent = stringBuilder.toString();
            if (!fileContent.isEmpty()) {
                savedText = fileContent;
                showToast("File opened successfully");
                fileOpened = true;
                fileChanged = false;
                btnViewFile.setEnabled(true);
                btnOpenFile.setEnabled(false);
                btnTransformXml.setEnabled(false);
            } else {
                showToast("File is empty");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast("Error reading file: " + e.getMessage());
        }
    }

    private void viewFile() {
        if (fileOpened) {
            btnEditText.setText(savedText);
            fileChanged = false;
            btnViewFile.setEnabled(false);
            btnTransformXml.setEnabled(true);

            Toast.makeText(this, "File viewed successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please open the file first", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveFile(String fileContent) {
        if (fileContent.isEmpty()) {
            showToast("Cannot save empty file");
            return;
        }

        if (validateXML(fileContent)) {
            try (FileOutputStream fos = openFileOutput(selectedFileName, MODE_PRIVATE)) {
                fos.write(fileContent.getBytes());

                savedText = fileContent;
                fileChanged = true;
                isTransformed = transformedFiles.contains(selectedFileName);
                transformedFiles.add(selectedFileName);

                showToast("File saved successfully");
                btnEditText.getText().clear();
                btnOpenFile.setEnabled(true);
                btnViewFile.setEnabled(false);
                btnTransformXml.setEnabled(false);
                btnCancelTransform.setEnabled(false);

            } catch (Exception e) {
                e.printStackTrace();
                showToast("Error saving file: " + e.getMessage());
            }
        } else {
            showToast("Invalid XML content");
        }
    }

    private boolean validateXML(String xmlContent) {
        try {
            Document doc = parseXmlDocument(xmlContent);

            if (checkForDuplicates(doc, "/products/product/id", "Duplicate product id found: ")) {
                return false;
            }

            if (checkForDuplicates(doc, "/products/product/name", "Duplicate product name found: ")) {
                return false;
            }

            if (checkForDuplicates(doc, "/products/product/description", "Duplicate product description found: ")) {
                return false;
            }

            return true;
        } catch (Exception e) {
            handleXmlValidationException(e);
            return false;
        }
    }

    private Document parseXmlDocument(String xmlContent) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xmlContent)));
    }

    private boolean checkForDuplicates(Document doc, String expression, String errorMessage) {
        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();
            XPathExpression expr = xpath.compile(expression);

            NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            Set<String> uniqueValues = new HashSet<>();

            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                String value = node.getTextContent().trim();
                if (uniqueValues.contains(value)) {
                    showToastDelayed(errorMessage + value, DELAY_SHORT);
                    return true;
                } else {
                    uniqueValues.add(value);
                }
            }
            return false;
        } catch (XPathExpressionException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void handleXmlValidationException(Exception e) {
        e.printStackTrace();
        showToast("Error validating XML: " + e.getMessage());
    }

    private void transformXml() {
        if (transformedFiles.contains(selectedFileName)) {
            showToast("The file has already been transformed.");
            return;
        }

        int xsltResourceId;
        String[] xsltFiles;

        if (selectedFileName.equals("library.xml")) {
            xsltFiles = new String[]{"Converting library"};
            xsltResourceId = R.raw.library_statistics;
        } else if (selectedFileName.equals("products.xml")) {
            xsltFiles = new String[]{"Converting products"};
            xsltResourceId = R.raw.transform_products;
        } else if (selectedFileName.equals("students.xml")) {
            xsltFiles = new String[]{"Converting students"};
            xsltResourceId = R.raw.transform_students;
        } else {
            showToast("Unsupported file selected.");
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Select XSLT Transformation")
                .setItems(xsltFiles, (dialog, which) -> {
                    applyXsltTransformation(xsltResourceId);
                })
                .show();
    }

    private void applyXsltTransformation(int xsltResourceId) {
        try {
            InputStream xsltStream = getResources().openRawResource(xsltResourceId);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(xsltStream));

            InputStream xmlStream = openFileInput(selectedFileName);
            StringWriter writer = new StringWriter();
            transformer.transform(new StreamSource(xmlStream), new StreamResult(writer));

            String result = writer.toString();
            btnEditText.setText(result);

            transformedFiles.add(selectedFileName);
            isTransformed = true;
            btnTransformXml.setEnabled(false);
            btnCancelTransform.setEnabled(true);

            showToast("Transformation successful");

            new Handler().postDelayed(() -> {
                if (selectedFileName.equals("library.xml")) {
                    showToast("This template contains the average price of books in the library and the total number of books in the library");
                } else if (selectedFileName.equals("products.xml")) {
                    showToast("В этом шаблоне содержится средняя цена книг в библиотеке и общее количество книг в библиотеке");
                } else if (selectedFileName.equals("students.xml")) {
                    showToast("This template contains students and their average grade");
                }
            }, DELAY_SHORT);

        } catch (Exception e) {
            e.printStackTrace();
            showToast("Error transforming XML: " + e.getMessage());
        }
    }


    private void cancelTransform() {
        if (isTransformed && transformedFiles.contains(selectedFileName)) {
            try (FileInputStream fis = openFileInput(selectedFileName)) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
                StringBuilder stringBuilder = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }

                String originalContent = stringBuilder.toString();
                btnEditText.setText(originalContent);

                transformedFiles.remove(selectedFileName);
                isTransformed = false;
                showToast("Transformation canceled");
                btnTransformXml.setEnabled(true);
                btnCancelTransform.setEnabled(false);

            } catch (IOException e) {
                e.printStackTrace();
                showToast("Error canceling transformation: " + e.getMessage());
            }
        } else {
            showToast("No transformation to cancel");
        }
    }

    private void closeApp() {
        if (fileChanged) {
            new AlertDialog.Builder(this)
                    .setTitle("Save changes")
                    .setMessage("You have unsaved changes. Do you want to save them before closing?")
                    .setPositiveButton("Save", (dialog, which) -> {
                        saveFile(savedText);
                        finish();
                    })
                    .setNegativeButton("Don't Save", (dialog, which) -> {
                        finish();
                    })
                    .setNeutralButton("Cancel", null)
                    .show();
        } else {
            finish();
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showToastDelayed(String message, int delay) {
        new Handler().postDelayed(() -> showToast(message), delay);
    }
}
