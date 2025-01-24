/***********/
/* PACKAGE */
/***********/
package IR;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/

public class IRcommandList
{
	public IRcommand head;
	public IRcommandList tail;

	IRcommandList(IRcommand head, IRcommandList tail)
	{
		this.head = head;
		this.tail = tail;
	}


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        IRcommandList current = this;
        
        while (current != null) {
            sb.append(current.head).append("\n");
            current = current.tail;
        }
        
        return sb.toString();
    }
}
