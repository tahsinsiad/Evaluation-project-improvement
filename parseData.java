package ParseData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class parseData {

	public static void main(String[] args) throws Exception {
		
		System.setProperty("webdriver.chrome.driver","F:\\Final\\chrome\\chromedriver.exe");
		
		WebDriver driver = new ChromeDriver();
		
		
		//WebDriver driver = new FirefoxDriver();
		
		System.out.println("Hitting the URL...");
		
		//Hit the URL
		driver.get("https://www.internetworldstats.com/top20.htm");
	
		driver.manage().window().maximize();
		
		List <WebElement> infoElements = null;
		
		//count rows
		List<WebElement> Rows = driver.findElements(By.xpath("/html/body/table[4]/tbody/tr/td/table/tbody/tr[1]/td/table/tbody/tr"));
		int totalRows = Rows.size();
		//System.out.println(" Total rows : "+totalRows);
		
		//count columns
		List<WebElement> Columns = driver.findElements(By.xpath("/html/body/table[4]/tbody/tr/td/table/tbody/tr[1]/td/table/tbody/tr[2]/td"));
		int totalColumns = Columns.size();
		//System.out.println(" Total Columns : "+totalColumns);
		
		List<String> countryList = new ArrayList<String>();
		List<Object[]> list = new ArrayList<Object[]>();
		
		System.out.println("Collecting the names of the countries...");
		//Extract data
		for(int i=3;i<=totalRows-4;i++){
			for(int j=2;j<=2;j++){
				WebElement dataCell = driver.findElement(By.xpath("/html/body/table[4]/tbody/tr/td/table/tbody/tr[1]/td/table/tbody/tr["+i+"]/td["+j+"]"));
				//System.out.println(dataCell.getText());
				countryList.add(dataCell.getText());
			}
		}
		
		//System.out.println("\n\n");
		
		//System.out.println(countryList.get(0));
		
//-------------- Saving data to the spreadsheet using Apache POI -------------------------
	    
	    XSSFWorkbook workBook = new XSSFWorkbook();
	    XSSFSheet sheet = workBook.createSheet();
	    String[] strings = null;

	    Map<String, Object[]> data = new TreeMap<String, Object[]>(); 
	    
	    int t=0,totalInfo,sum=0;
	    WebElement element2 = null;
		
		
		//-------------------- Searching the First element Like China in Google ----------------
		
		for (int i = 0; i < countryList.size(); i++) {
		 totalInfo=0;
		System.out.println("Searching "+countryList.get(i)+" on Google...");
		driver.get("https://www.google.com/?hl=en");
	    WebElement element = driver.findElement(By.name("q"));
	    element.sendKeys(countryList.get(i)); // send also a "\n"
	    element.submit();
	    
		
	    
	    //---------------------------------------------------------------------------------------
	    
	    
	    //---------------- Parsing the Information which are at the right side of the page ------------
	    
	    infoElements = driver.findElements(By.xpath(".//div[@class='zloOqf PZPZlf kno-fb-ctx']"));
		
	    totalInfo = infoElements.size();
	    //System.out.println(totalInfo);
	    
	    System.out.println("Retrieving all the information...");
	    
	    //----------------------------------------------------------------------------------------------
	    
	    
	    System.out.println("Mapping the information...");
	    
	    
	    list.add(new Object[] {countryList.get(i)});
	    list.add(new Object[] {"\n"});
	    for(int j=0;j<totalInfo;j++)
	    {
	    	element2 = infoElements.get(j);
	    	String contentString = element2.getText();
	    	strings=contentString.split(":",2);
	    	//data.put(String.valueOf(t),new Object[] {strings[0],strings[1]});
	    	list.add(new Object[] {strings[0],strings[1]});
	    	
	    	    	
	    	//System.out.println(contentString);
	    }
	    
	    list.add(new Object[] {"\n"});
	    
	    
	    try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		}
		
		//System.out.println(t);
		
	    
	    //------------------------------------------------------------------------------------------
	    
	    Set<String> keyset = data.keySet(); 
	    
	    //Setting width of the cell size
	    int width = 30;
	    sheet.setDefaultColumnWidth(width);
	    
	    //Set the font BOLD
	    XSSFFont font = workBook.createFont();
	    font.setBold(true);
	    
	    XSSFCellStyle style = workBook.createCellStyle();
	    style.setFont(font);
	    
	    XSSFCellStyle style1 = workBook.createCellStyle();
	    style1.setWrapText(true);
	    
	    
	    System.out.println("Writing data to the spreadsheet...");
	    
	    
	    //-------------------- Sending Data to the spreadsheet -----------------------------
	    
	    int row=0;
	    for(int q=0;q<list.size();q++)
	    {
	    	XSSFRow rows = sheet.createRow(row++);
	    	Object[] objects = list.get(q);
	    	
	    	int cellno=0;
	    	for(Object o:objects)
	    	{
	    		XSSFCell cell = rows.createCell(cellno++);
	    		cell.setCellValue(o.toString());
	    		cell.setCellStyle(style1);
	    		
	    		//Making the 1st Column BOLD
	    		if(cellno%2==1)
	    		{
	    			cell.setCellStyle(style);
	    		}
	    	}
	    }
	    
	    try {
	    	
	    	FileOutputStream fileOutputStream = new FileOutputStream(new File("Country.xlsx"));
		    workBook.write(fileOutputStream);
		    fileOutputStream.close();
		    System.out.println("DONE! Excel File is ready.");
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	    
	    //---------------------------------------------------------------------------------------------

	}

}
