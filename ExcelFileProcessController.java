/**================================================================
* Cisco Systems, Inc. (Lancope, LLC.)
* Name: BayBridge
* Description: Controller for ExcelFileProcess.vfp
* Created Date: 05-14-2018  
* Created By: Saurabh Sisodia(sasisodi@cisco.com)
* Requirment: 1.    Update Contacts with Flexera information
2.	Update Accounts with Flexera information
3.	Update Accounts with Cisco Information
4.	Webinar Campaign Members
* ===============================================================*/

public class ExcelFileProcessController {
    Transient public string selectedProcessDisplayName{get;set;}
      public List<File_Process__c> flexeraDownloadVList{get;set;}
    Transient public Set<String> flexeraSetListAccount;
    Transient public Set<String> flexeraDownloadVSet; 
    Transient public Set<ID> flexeraAccountIdSet;
    Transient public List<File_Process__c> uniqueAccountList {get;set;}
    Transient public List<File_Process__c> finalList {get;set;}
    public Map<string, date> mapFlexeraFileDD;
    public Set<String> emailList {get; set;}
    Transient public integer finalCount {get; set;}
    Transient public integer uniqueAccountListCount {get; set;}
    Transient public string contactInformationBlockRender{get;set;}
 //   public string snoAccoutBlockRender {get;set;}
 //   public string snoFileProcessBlockRender{get;set;}
    Transient public string recordsDisplayBlockIsRendered {get; set;}
    Transient public integer validRecordsCountForAccount {get;set;}
    Transient public List<AggregateResult> flexeraDownloadVListCount;
    Transient Public integer ddPresent {get; set;}
    Transient public string showAllRecordsIsrendered {get; set;}
    Transient public string showValidAccountsIsrendered {get; set;}
    Transient public string uniqueAccountIdsBlockIsRender {get;set;}
    Transient public integer finalContactIdCount {get;set;}
    Transient public List<wrapper> lstWrapper {get;set;}
    Transient public List<contact> contactListToUpdate {get;set;}
    Transient public string contactIdProcessIsRerendered {get;set;}
  //  public List<exportListWrpr> exportListWrapper {get;set;}
    public List<contact> newcn {get;set;}
    Transient public List<Account> accListToUpdate;    
    Transient public Map<id,Account> snoAccMap{get;set;}
   
    public class exportListWrpr{
        public string Name {get; set;}
        public string cnEmail {get;Set;}
        public date downloadDate {get; set;}
    }
    
    public class wrapper{
        public string cnID {get;set;}
        public string cnEmail {get;set;}
        public string dd {get;set;}
        public string cnName {get;set;}
        public wrapper(string cnId, String a, string d, string cn)
        {
            this.cnID = cnId;
            this.cnEmail = a;
            this.dd = d;
            this.cnName = cn;
        }
    }
    

    //Constructor
    public ExcelFileProcessController()
    {
        system.debug('ExcelFileProcessController Starts');
        
        string selectedProcess = ApexPages.currentPage().getParameters().get('sv');
        displayProcess(selectedProcess);
        
        
        flexeraDownloadVList = new List<File_Process__c>();
        contactInformationBlockRender = 'false';
        
        recordsDisplayBlockIsRendered = 'false';
        showAllRecordsIsrendered = 'false';
        contactIdProcessIsRerendered = 'false';
        //  lstWrapper = new List<wrapper>();
        newcn = new List<contact>();
        
        showSelectedProcessBlock(selectedProcess);
        flexeraDownloadVListCount = [SELECT COUNT(Download_Date__c) FROM  File_Process__c];
        
        
        ddPresent = (integer)flexeraDownloadVListCount[0].get('expr0');
        
        if(ddPresent > 0)
        {
            if(selectedProcess == 'DownloadDateUpdate'){
                system.debug('aaa');
                flexeraDownloadVList = [Select id, Name,Account_Name__c,Account_ID__c, Download_Date__c,Product_Version__c, Email__c,File_UDF_Value_2__c,concatenate__c from File_Process__c where Account_ID__c like '00%' AND (Account_ID__c like '__________________' OR Account_ID__c like '_______________') Order By Email__c, Download_Date__c Desc ]; 
                
                system.debug('flexeraDownloadVList::'+flexeraDownloadVList.size());  
            } 
        }
    }
    
    
    
    //Method to get the Selected Process and display the Name
    public String displayProcess(string selectedProcess)
    {
        
        if(selectedProcess == 'DownloadDateUpdate')
        {
            selectedProcessDisplayName = 'Update Contacts with Flexera information';
        }
        else if(selectedProcess == 'UDFValueUpdate')
        {
            selectedProcessDisplayName = 'Update Accounts with Flexera information';
        }
        else if(selectedProcess == 'AccountSNOUpdate')
        {
            selectedProcessDisplayName = 'Update Accounts with Cisco Information';            
        }
        else if(selectedProcess == 'CampaigneMemberUpdate')
        {
            selectedProcessDisplayName = 'Update Campaigne Members';     
        }
        if(selectedProcess == 'AccAdmin')
        {
            selectedProcessDisplayName = 'Flexera Membership Update';
        } 
        else{
            ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.ERROR,'No Value passed in URL');
        }
        return selectedProcessDisplayName;
    }
    
    //Method to return to FileProcesstool page 
    public pagereference goBack()
    {
        pagereference pager = new pagereference('/apex/FileProcessTool');
        pager.setRedirect(true);
        return pager;   
    }
    
    //Method to show the page block on the basis of Selected process
    public void showSelectedProcessBlock(String selectedProcess)
    {
        if(selectedProcess == 'DownloadDateUpdate')
        {
            contactInformationBlockRender = 'true';  
        }
        
    }
    
    public void extractRecords()
    {
        showAllRecordsIsrendered = 'false';
        contactIdProcessIsRerendered = 'false';
        mapFlexeraFileDD = new Map<string, date>();
        flexeraDownloadVSet = new Set<String>();
        flexeraAccountIdSet = new Set<ID>();
        emailList = new Set<String>();
        finalList = new List<File_Process__c>();
        integer i=0;
        try{
            if(!flexeraDownloadVList.isEmpty())
            {
                for(File_Process__c f:flexeraDownloadVList)
                {
                    if(flexeraDownloadVSet.add(f.concatenate__c))
                    {
                        flexeraAccountIdSet.add(f.Account_ID__c);
                       emailList.add(f.Email__c);
                         mapFlexeraFileDD.put(f.Email__c, f.Download_Date__c);
                        if(i<400){
                             finalList.add(f);
                            i++;
                        }
                        
                    }
                    
                }
                finalList.sort();
                finalCount = flexeraAccountIdSet.size();
                
                recordsDisplayBlockIsRendered = 'true';  
            }
            else{
                recordsDisplayBlockIsRendered= 'false';
                ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.ERROR,'List is empty. Please import list using data loader');
                Apexpages.addMessage(myMsg);
            }
        }
        catch(Exception e){             
            ApexPages.addMessages(e);
        }
    }
    
    public void availableContactRecords()
    {     
        system.debug('a');
        try{
            system.debug('b');
           // extractRecords();
            recordsDisplayBlockIsRendered = 'false';
            showAllRecordsIsrendered = 'false';
            contactIdProcessIsRerendered = 'true';
            
     /*       for(File_Process__c f: finalList)
            {
                mapFlexeraFileDD.put(f.Email__c, f.Download_Date__c);
            } */
			
            List<contact> lc = new List<contact>();
            contactListToUpdate = new List<contact>();
            // not getting correct data, have to pass correct contact Ids.
            lc = [Select id, email,name from contact where Email =:emailList];
           
            lstWrapper = new List<wrapper>();
            Map<string, ID> mapContactEmail = new Map<string, Id>();
            Set<string> flexeraEmailSet1 = new Set<string>();
            integer i=0;
            integer count = 0;
            for(contact c:lc)
            {
                if(flexeraEmailSet1.add(c.Email)){

                if(mapFlexeraFileDD.containsKey(c.Email) )
                {
                    if(i<400){
                   lstWrapper.add(new Wrapper(c.id,c.Email,mapFlexeraFileDD.get(c.Email).format(),c.Name));
                    }
                  
                 contactListToUpdate.add(c);
                    c.Last_Flexera_Download_Date__c = mapFlexeraFileDD.get(c.Email);
                    c.Flexera_Member__c = true;
					newcn.add(c); 
                }                    
                }
               count++;
            }
            finalContactIdCount = newcn.size();
            system.debug('lcsize::'+lc.size());
            system.debug('count::'+ count);
            system.debug('mapFlexeraFileDD::'+mapFlexeraFileDD.size());
		//	system.debug('flexeraDownloadVSet::'+flexeraDownloadVSet.size());
        //    system.debug('flexeraAccountIdSet::'+flexeraAccountIdSet.size());
        //    system.debug('lstWrapper.size():::'+lstWrapper.size());
        }
        catch(Exception e)
        {
            ApexPages.addMessages(e);
        }
    }
    
    //This method is: button click call ExcelSheetPOC.vfp for contact download date
    public pagereference redirectToExcelSheetDownload()
    {
        pagereference pager = new pagereference('/apex/ExcelSheetPOC');
        return pager;
    }
    
    
    public void saveFlexeraDownloadDatetoContact()
    {
        if(newcn.size()>0)
        {
          /*  system.debug('contact list to update::'+newcn.size());
            for(contact c: newcn)
            {
                system.debug(c);
            }
         //   update newcn;
            ApexPages.AddMessage( new ApexPages.Message(ApexPages.Severity.CONFIRM,'Contact Records Updated')); */
             ApexPages.AddMessage( new ApexPages.Message(ApexPages.Severity.CONFIRM,'Batch is running to update the contact information. you will get email once updates are done.'));
            ExcelFileProcessControllerBatchApex c = new ExcelFileProcessControllerBatchApex();
        	Database.executeBatch(c, 1000);
        }     
    } 
    
}