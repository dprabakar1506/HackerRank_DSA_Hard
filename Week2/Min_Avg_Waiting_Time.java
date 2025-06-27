import java.util.*;
public class solution {

    public static void main(String[] args) {
        //Inputs 
        Scanner in = new Scanner(System.in);
        int numOfCustomers = in.nextInt(); //number of customer
        Customer[] customers = new Customer[numOfCustomers]; //Customer Array

        for (int i = 0; i < numOfCustomers; i++) {
            int orderTime = in.nextInt();
            int cookTime = in.nextInt();
            customers[i] = new Customer(orderTime, cookTime);
        }
        in.close();
        //Solution starts
        Arrays.sort(customers, Comparator.comparingInt(o -> o.orderTime)); //Sorting the inputs on basis of order time

        Queue<Customer> waitTime = new PriorityQueue<>(); //Min heap
        long currentTime = 0L;
        long totalWaitTime = 0L;
        int index = 0;

        while (!waitTime.isEmpty() || index < customers.length) {
            while (index < customers.length && customers[index].orderTime <= currentTime) {
                waitTime.add(customers[index]);
                index++;
            }
            if (waitTime.isEmpty()) {
                currentTime = customers[index].orderTime;
                continue;
            }

            Customer served = waitTime.poll();
            currentTime += served.cookTime;
            totalWaitTime += currentTime - served.orderTime;
        }

        System.out.println(totalWaitTime / customers.length);
    }

    //Customer class for ease to solve the question
    static class Customer implements Comparable<Customer> {
        int orderTime;
        int cookTime;

        public Customer(int orderTime, int cookTime) {
            this.orderTime = orderTime;
            this.cookTime = cookTime;
        }
        //overriding the operator
        @Override
        public int compareTo(Customer o) {
            if (this.cookTime > o.cookTime) {
                return this.orderTime;
            } else
                return -1;
        }
    }
}
