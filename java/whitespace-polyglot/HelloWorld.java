
import java.util.*;	

class HelloWorld	{

 	static final Stack<Character> MESSAGE = new Stack<>();

 	static {     
  MESSAGE.push( '\0' );      
  MESSAGE.push( '\n' );  	 	 
  MESSAGE.push( '!' );	    	
  MESSAGE.push( 'd'	);	  	  
  MESSAGE.push( 'l'	);	 		  
  MESSAGE.push( 'r'	);		  	 
  MESSAGE.push( 'o'	);	 				
  MESSAGE.push( 'w'	);		 			
  MESSAGE.push( ' '	);     
  MESSAGE.push( ',' );	 		  
  MESSAGE.push( 'o'	);	 				
  MESSAGE.push( 'l'	);	 		  
  MESSAGE.push( 'l'	);	 		  
  MESSAGE.push( 'e'	);	  	 	
  MESSAGE.push( 'H'	);  	   

 	}

 	public static void main(String[] args) { 

 	  	print();

 	}




  static void print()	{
	System.out.print(
  MESSAGE.pop() );
 MESSAGE.push(MESSAGE.peek());
if	(MESSAGE.pop() =='\0')return;

 print();
  }	
}
        

  

	



