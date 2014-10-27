package pico;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextService implements Runnable {
	public TextService() {}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("This is service");
	}


	@Pico(timeout=5000)
	public void service1(int n) {
		System.out.println("Starting service1");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			//
		}
		System.out.println("Finished service1 " + n);
	}
	
	@Pico(timeout=3000)
	public void service2() {
		System.out.println("Starting service2");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			//
		}
		System.out.println("Finished service2");
	}
	
	@Pico(timeout=3000)
	public List<Integer> service3() {
		System.out.println("Starting service3");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			//
		}
		System.out.println("Finished service3");
		return Arrays.asList(new Integer[] {1,2});
	}

    @Pico(timeout=3000)
    public void service4(List<Integer> list) {
        System.out.println("Starting service4");
        try {
            System.out.println("in service4 using a value returned from service3 " + list.get(0));
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            //
        }
        System.out.println("Finished service4");
    }
}
