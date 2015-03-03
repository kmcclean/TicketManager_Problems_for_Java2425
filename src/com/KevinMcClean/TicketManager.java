package com.KevinMcClean;

import java.io.FileWriter;
import java.util.*;
import java.io.IOException;

/**
 * Created by Kevin on 2/26/2015.
 */

public class TicketManager {

    public static void main(String[] args) throws IOException{
        //this tracks the two sets of tickets.
        LinkedList<Ticket> ticketQueue = new LinkedList<Ticket>();
        LinkedList<Ticket> resolvedTickets = new LinkedList<Ticket>();

        Scanner scan = new Scanner(System.in);

        while (true) {
            //this chooses which method will be run.
            System.out.println("1. Enter Ticket\n2. Delete by Description\n3. Delete by ID\n4. Display All Tickets" +
                    "\n5. Search by Keyword\n6. Quit");
            int task = Integer.parseInt(scan.nextLine());

            if (task == 1) {
                //Call addTickets, which will let us enter any number of new tickets
                addTickets(ticketQueue);

            } else if (task == 2) {
                //delete a ticket
                textDelete(ticketQueue, resolvedTickets);

            } else if (task == 3) {
                deleteID(ticketQueue, resolvedTickets);

            } else if (task == 4){
                printAllTickets(ticketQueue);
            }
            else if (task == 5){
                textSearch(ticketQueue);
            }
            else if (task == 6) {
                //Quit. Future prototype may want to save all tickets to a file
                quittingTime(ticketQueue, resolvedTickets);
                System.out.println("Quitting program");
                break;
            }
            else{
                //this will happen for 3 or any other selection that is a valid int
                //TODO Program crashes if you enter anything else - please fix
                //Default will be print all tickets
                printAllTickets(ticketQueue);
            }
        }

        scan.close();

    }

    protected static void printAllTickets(LinkedList<Ticket> tickets) {
        System.out.println(" ------- All open tickets ----------");
        //this prints all of the tickets available.
        for (Ticket t : tickets) {
            System.out.println(t); //Write a toString method in Ticket class
            //println will try to call toString on its argument
        }
        System.out.println(" ------- End of ticket list ----------");

    }

    protected static void deleteID(LinkedList<Ticket> ticketQueue, LinkedList<Ticket> closedTickets) {
        printAllTickets(ticketQueue);   //display list for user

        if (ticketQueue.size() == 0) {    //no tickets!
            System.out.println("No tickets to delete!\n");
            return;
        }

        Scanner deleteScanner = new Scanner(System.in);
        //This while loop makes the user continue until they have deleted a ticket.
        while (true) {
            System.out.println("Enter ID of ticket to delete");
            int deleteID = deleteScanner.nextInt();
            //Loop over all tickets. Delete the one with this ticket ID
            boolean found = false;
            for (Ticket ticket : ticketQueue) {
                if (ticket.getTicketID() == deleteID) {
                    found = true;
                    //has the user explain why the ticket was closed.
                    System.out.println("Why has this ticket been closed?");
                    String answer = deleteScanner.next();
                    Date dateClosed = new Date();
                    //creates an answer string.
                    answer = answer + "Closed On: " + dateClosed.toString();
                    //extends the description to contain this new information about the ticket.
                    ticket.amendDescription(answer);
                    //adds it to the closedTickets List, and removes it from the ticketsQueue.
                    closedTickets.add(ticket);
                    ticketQueue.remove(ticket);
                    System.out.println(String.format("Ticket %d deleted", deleteID));
                    printAllTickets(ticketQueue);  //print updated list
                    return; //don't need loop any more.
                }
            }
            //makes the user keep trying until they pick a ticket to delete.
            if (found == false) {
                System.out.println("Ticket ID not found, no ticket deleted. Please enter an existing ticket ID.");
            }
        }


    }

    protected static void addTickets(LinkedList<Ticket> ticketQueue) {
        Scanner sc = new Scanner(System.in);
        boolean moreProblems = true;
        String description, reporter;
        Date dateReported = new Date(); //Default constructor creates date with current date/time
        int priority;

        while (moreProblems) {

            System.out.println("Enter problem");
            description = sc.nextLine();
            System.out.println("Who reported this issue?");
            reporter = sc.nextLine();
            System.out.println("Enter priority of " + description);
            priority = Integer.parseInt(sc.nextLine());

            Ticket t = new Ticket(description, priority, reporter, dateReported);
            addTicketInPriorityOrder(ticketQueue, t);

            printAllTickets(ticketQueue);

            System.out.println("More tickets to add?");
            String more = sc.nextLine();
            if (more.equalsIgnoreCase("N")) {
                moreProblems = false;
            }
        }
    }

    protected static void addTicketInPriorityOrder(LinkedList<Ticket> tickets, Ticket newTicket) {

        //Logic: assume the list is either empty or sorted

        if (tickets.size() == 0) {//Special case - if list is empty, add ticket and return
            tickets.add(newTicket);
            return;
        }

        //Ticket with the HIGHEST priority number go at the front of the list. (e.g. 5=server on fire)
        //Ticket with the LOWEST value of their priority number (so the lowest priority) go at the end

        int newTicketPriority = newTicket.getPriority();

        for (int x = 0; x < tickets.size(); x++) {    //use a regular for loop so we know which element we are looking at

            //if newTicket is higher or equal priority than the this element, add it in front of this one, and return
            if (newTicketPriority >= tickets.get(x).getPriority()) {
                tickets.add(x, newTicket);
                return;
            }
        }

        //Will only get here if the ticket is not added in the loop
        //If that happens, it must be lower priority than all other tickets. So, add to the end.
        tickets.addLast(newTicket);
    }

    protected static void textDelete(LinkedList<Ticket> ticketQueue, LinkedList<Ticket> closedTickets) {
        Scanner searchScan = new Scanner(System.in);
        //has the user input which search term to find.
        System.out.println("Enter the term to delete");
        String deletion = searchScan.next();
        ArrayList<Ticket> deletionList = new ArrayList<Ticket>();
        //runs through the list of tickets and finds the ones which have the search term. User must provide reason form them being closed.
        for (Ticket ticket : ticketQueue) {
            String ticketDesc = ticket.getDescription();
            if (ticketDesc.contains(deletion)) {
                System.out.println("Why has this ticket been closed?");
                String answer = searchScan.next();
                Date dateClosed = new Date();
                answer = answer + "Closed On: " + dateClosed.toString();
                ticket.amendDescription(answer);
                closedTickets.add(ticket);
                ticketQueue.remove(ticket);
            }
        }
    }
//this prints out all the tickets which have the requested ticket.
    protected static void textSearch(LinkedList<Ticket> ticketQueue) {
        Scanner searchScan = new Scanner(System.in);
        System.out.println("Enter the term to search");
        String searchTerm = searchScan.next();
        System.out.println(" ------- All tickets containing \"" + searchTerm + "\"----------");
        //this loop does the search for the ticket.
        for (Ticket ticket : ticketQueue) {
            String ticketDesc = ticket.getDescription();
            if (ticketDesc.contains(searchTerm)) {
                System.out.println(ticket);
            }
        }
        System.out.println(" ------- End of ticket list ----------");
    }

    protected static void quittingTime(LinkedList<Ticket> openTickets, LinkedList<Ticket> closedTickets) throws IOException{

        //creates an open_tickets file and adds every member of the ticketQueue(called openTickets here) to the file.
        FileWriter openTicketReport = new FileWriter("open_tickets.txt");
        for (Ticket ticket:openTickets){
            openTicketReport.write(ticket.toString()+"\n");
        }
        openTicketReport.close();

        //gets the date and time.
        Date fileDate = new Date();
        //creates an "Closed Reports As Of [Date]" file and adds every member of the resolvedTickets(called...
        //...closedTickets here) to the file.
        String fileName = "Closed Reports As Of " + fileDate.toString() + ".txt";
        FileWriter closedTicketReport = new FileWriter(fileName);
        for (Ticket ticket: closedTickets){
            closedTicketReport.write(ticket.toString()+"\n");
        }
        closedTicketReport.close();
    }
}


