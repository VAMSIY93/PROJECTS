package implementation;

public class Message {
	
	public Message(){
		
	}
	public static enum msgEnum 
	{ FIND_SUCCESSOR, //0
		PUT_SUCCESSOR, //1
		GET_PREDECESSOR,//2
		PUT_PREDECESSOR,//3
		NOTIFY_NEW_PREDECESSOR,//4 
		NOTIFY_NEW_SUCCESSOR,//5
		STABILIZE,//6
		EMPTY}//7
	
	public Node srcNode; // Originator of the message
	public Node sender; // the one who writes message in someone else queue
	public Node destNode; // Node which will be receiving it
	public msgEnum msg_enum; // message descriptor
	public Object data;// actual message (can be a node or a key)
	
	public String msgEnumToString(msgEnum e)
	{
		String message=null;
		switch(e){
		case FIND_SUCCESSOR:
			message="FIND_SUCCESSOR"; break;
		case PUT_SUCCESSOR:
			message="PUT_SUCCESSOR"; break;
		case GET_PREDECESSOR:
			message="GET_PREDECESSOR"; break;

		case PUT_PREDECESSOR:
			message="GET_PREDECESSOR"; break;

		case NOTIFY_NEW_PREDECESSOR:
			message="NOTIFY_NEW_PREDECESSOR"; break;

		case NOTIFY_NEW_SUCCESSOR:
			message="NOTIFY_NEW_SUCCESSOR"; break;
		case STABILIZE:
			message="STABILIZE"; break;
		case EMPTY:
			message="EMPTY"; break;
			
		}	
		return message;
	}
	/**
	 *  Below are the redundant fields which needs to be removed
	 */
	/**
	DHTKey inKey; // used for getting successor
	String msg_type;// GET_SUCCESOR, GET_PREDECESSOR, INIT_FINGER_TABLE
	Node dataNode;
	**/
	
	}
