public class MessageQueue {
    
    private int capacity;//Number of messages that can be stored in the queue.

    private Vector<T> queue = new Vector<T>();//The queue for receiving all incoming messages.

    /**
     * Constructor, initializes the queue.
     * 
     * @param capacity The number of messages allowed in the queue.
     */
    public MessageQueue(int capacity) {
        this.capacity = capacity;
    }

    
    public synchronized void send(T message) {
        //TODO check
    }
	
    public synchronized T receive() {
        //TODO check
        
	return 0;
    }
 
    private boolean isEmpty() {
        return queue.size() == 0;
    }
}
