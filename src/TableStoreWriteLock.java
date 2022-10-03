import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.LongConsumer;


// TODO: implement the methods in this class
public class TableStoreWriteLock implements iWriteLock{

    @Override
    public void lock() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void unlock() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean forceUnlockIfProcessIsDead() {
        return false;
    }

    @Override
    public boolean isLockedByCurrentProcess(LongConsumer notCurrentProcessConsumer) {
        // TODO Auto-generated method stub
        return false;
    } 
}
