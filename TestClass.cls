//this is new updation in this class document
// just to test git hub
// I am not sure if it is woorking
//will it work this time?

public class TestClass{
    public string empFirstName {get;set;}
    public string empLastName {get;set;}
    public string progLang {get;set;}
    public list<Employee__c> empList {get;set;}
    public boolean selected {get; set;}
    public string query {get;set;}
    public String selectedVal{get;set;}  // This will hold the selected value, the id in here
  
    
    public List<SelectOption> getopenPresentationOptions(){
        List<SelectOption> optns = new List<Selectoption>();
        Schema.DescribeFieldResult fieldResult = Employee__c.Prog_Lang__c.getDescribe();
        List<Schema.PicklistEntry> ple = fieldResult.getPicklistValues();
        for( Schema.PicklistEntry f : ple)
        {
            optns.add(new SelectOption(f.getLabel(), f.getValue()));
        }   
        return optns;
    }
    
    public Testclass()
    {
        selected = false;
        query = 'select id,name, First_Name__c, Last_Name__c,Email__c, Phone_No__c, Start_Date__c,DOB__c,Prog_Lang__c,isselected__c from Employee__c order by id desc';
        empList = database.query(query);
    }
    
    public String debugSoql {
        get { return query; }
        set;
    }
    
    public void abc()
    {
    
    }
    public void search()
    {
        
        query = 'select id,name, First_Name__c, Last_Name__c, Phone_No__c, Start_Date__c,DOB__c,Prog_Lang__c,isselected__c from Employee__c where Employee__c.First_Name__c != NULL ';
        if(!empFirstName.equals(''))
        {
            query += 'and First_Name__c like \'%'+empFirstName+'%\'';                  
            
        }
        if(!empLastName.equals(''))
        {
            query += 'and Last_Name__c like \'%'+empLastName+'%\'';                  
        }
        if(!selectedVal.equals(''))
        {
            query += 'and Prog_Lang__c includes (\''+selectedVal+'\')';                  
        }
        //  system.debug('Prog_Lang__c' +query);
        empList = database.query(query);
    }
    
    public void Clear()
    {
        empFirstName = '';
        empLastName = '';
        string query = 'select id,name, First_Name__c, Last_Name__c, Phone_No__c,DOB__c, Start_Date__c from Employee__c';
        empList = database.query(query);
    }
    
    public pagereference Create()
    {
        pagereference pager = new pagereference('/apex/emp_newform');
        pager.setRedirect(true);
        return pager;
    }
    
    public void SeeResults()
    {
        if(!selected)
        {
            ApexPages.addMessage(new ApexPages.message(ApexPages.severity.ERROR,'not selected'));
        }
        else
        {
            ApexPages.addMessage(new ApexPages.message(ApexPages.severity.ERROR,'selected'));
        }
        
        
    }
    
    public pagereference DeleteDuplicate()
    {            
        //for(integer i =0; i< emplist.size();i++)
        list<employee__c> abc = new list<employee__c>();
        
        for(employee__c c: emplist)
        {
            /* if(abc. contains(c.first_name__c))
{
system.debug('not Present');
}*/
            
            
        }
        
        return null;
        
    }
    
    
}