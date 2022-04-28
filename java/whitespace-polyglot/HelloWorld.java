import java.util.*; 
 

class HelloWorld {

 	static final List<String> MESSAGE = new ArrayList<>();

 	static {     

 	MESSAGE.addAll(List.of(
      	"\n", 	 
    	"!",    	
   		"d",  	  
   		"l", 		  
   		"r",	  	 
   		"o", 				
   		"w",	 			
    	" ",    
    	",", 		  
   		"o", 				
   		"l", 		  
   		"l", 		  
   		"e",  	 	
   	  	"H"   

 	));

 	}

 	public static void main(String[] args) { 

 	    Collections.reverse( MESSAGE );

 	  	print();

 	}




  static void print()	{
	System
   .out.print(
 
	String.join("", MESSAGE)

 );
  	}
}
        

  

	
