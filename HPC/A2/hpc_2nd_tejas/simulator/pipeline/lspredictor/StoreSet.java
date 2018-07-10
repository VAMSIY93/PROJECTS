package pipeline.lspredictor;

import java.io.FileWriter;
import java.io.IOException;

import config.EnergyConfig;

import pipeline.ExecutionEngine;
import generic.Core;

/*Class Interface LSPredictor
 * Has to be implemented by all types of LSPredictor
 */
public class StoreSet extends LSPredictor {
    
    public long accuratepredictionCount=0;
    public long totalpredictionCount=0;
    long SSIT[];
    long LFST[];
    int CHTsize;
    ExecutionEngine containingExecutionEngine;
    long numAccesses;
    long lastPredictedAddress;
    long lastStoreAddress;
    boolean lastPredictionDecision;
    int currentSetID;
    
    public StoreSet(ExecutionEngine containingExecutionEngine) {
        super(containingExecutionEngine);   
        CHTsize = 4096;
        SSIT = new long[CHTsize];
        LFST = new long[CHTsize];
        
        for (int i=0;i<CHTsize;i++)
        {
            SSIT[i] = -1;
            LFST[i] = -1;
        }
        currentSetID = 1;
    }
    
  /**
   *
   * @param actualaddress is actual address of operand 1 
   * @param predictaddress is store address on which the load was predicted to be dependent
   * @param wasForwarded boolean variable indicating whether the load was forwarded or not 
   * <code>true</code> when forwarded  otherwise <code>false</code>
   */
    public void  Train(long storePC,boolean x , boolean y, long loadPC,boolean hasMispredicted)
    {       
        totalpredictionCount++;
        if (loadPC<0 || storePC<0)  return;
        
        if (SSIT[(int)(loadPC % CHTsize)] > 0 && SSIT[(int)(storePC % CHTsize)] > 0 
                && SSIT[(int)(loadPC % CHTsize)] == SSIT[(int)(storePC % CHTsize)])
        {
            LFST[(int)(SSIT[(int)(storePC % CHTsize)]%CHTsize)]=storePC;
        }
        else if (SSIT[(int)(loadPC % CHTsize)] == -1 && SSIT[(int)(storePC % CHTsize)] == -1)
        {
            SSIT[(int)(loadPC % CHTsize)] = currentSetID;
            SSIT[(int)(storePC % CHTsize)] = currentSetID;
            
            currentSetID++;
        }
        else if (SSIT[(int)(loadPC % CHTsize)] > 0 && SSIT[(int)(storePC % CHTsize)] == -1)
        {
            SSIT[(int)(storePC % CHTsize)] = SSIT[(int)(loadPC % CHTsize)];         
        }
        else if (SSIT[(int)(loadPC % CHTsize)] == -1 && SSIT[(int)(storePC % CHTsize)] > 0)
        {
            SSIT[(int)(loadPC % CHTsize)] = SSIT[(int)(storePC % CHTsize)];         
        }
        else if (SSIT[(int)(loadPC % CHTsize)] > 0 && SSIT[(int)(storePC % CHTsize)] > 0 && 
                SSIT[(int)(loadPC % CHTsize)] != SSIT[(int)(storePC % CHTsize)])
        {            
            if (SSIT[(int)(loadPC % CHTsize)] > SSIT[(int)(storePC % CHTsize)])
            {
                SSIT[(int)(loadPC % CHTsize)] = SSIT[(int)(storePC % CHTsize)];
            }
            else
            {
                SSIT[(int)(storePC % CHTsize)] = SSIT[(int)(loadPC % CHTsize)];
            }           
        }               
        
    }
 /**
  *
  * @param address takes in the load's resolved address  
  * NOTE : the outcome field is useful only in the NoPredictor and PerfectPredictor cases
  */
    public long predict(long addressLoad /*load PC*/,long storeOperand /*Store Addr*/)
    {   
        if (addressLoad<0) return -1;
        
        long SSIDX = SSIT[(int)(addressLoad % CHTsize)];
        if (SSIDX == -1)
        {
            return -1;
        }
        else
        {
            if (LFST[(int)(SSIDX % CHTsize)] == -1)
            {
                return -1;
            }
            else
            {
                return LFST[(int)(SSIDX % CHTsize)];
            }
        }       
    }
/*    
    public void incrementAccuracy()
    {
        accuratepredictionCount++;
        totalpredictionCount++;
    }
    
    public double accuracy()
    {
        return ((double)accuratepredictionCount/(double)totalpredictionCount)*100.0;
    }
*/

}
