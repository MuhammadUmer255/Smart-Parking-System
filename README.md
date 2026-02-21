# Smart Parking System with Analytics

A Java Swing–based *Smart Parking System* developed as a *DSA Semester Project*.  
This system efficiently manages parking slots for *Cars, Bikes, and Trucks* using fundamental *Data Structures* and provides real-time parking status with a graphical interface.

# Project Information

- **Course:** Data Structures & Algorithms (DSA)
- **Project Type:** Semester Project
- **Technology:** Java (Swing GUI)
- **Architecture:** Model–View–Controller (MVC)

#  Team Members
- **Kashif Idrees**   
- **Muhammad Umar** 
- **Rana Subhan Iqbal** 

# Features

✔ Park vehicles (Car / Bike / Truck)  
✔ Reserve specific parking slots  
✔ Cancel reservations  
✔ Check-out parked vehicles  
✔ View live parking status  
✔ Color-coded slot representation  
✔ Simple & interactive GUI  

# Slot Color Coding
Color Status
🟢 Green Available 
🔴 Red Occupied 
🔵 Blue Reserved 

# Data Structures Used
 `HashMap<String, String>` Maps vehicle number to parking slot
 `int[][]` Arrays  Parking slot grids for each vehicle type 
 Swing Components  GUI interaction 

# Why HashMap?
Fast lookup, insertion & deletion  
**O(1)** time complexity for check-in and check-out  

# User Interface Components

- JFrame
- JPanel
- JButton
- JLabel
- JTextField
- JComboBox
- JTextArea  

**Layouts Used:**
- FlowLayout  
- BorderLayout  
- GridLayout  

# Scalability

- Slot grid size can be increased easily  
- Can be extended with:
- Database integration  
- Object-based slot management  
- Time-based parking analytics
 
# Conclusion

The **Smart Parking System** demonstrates effective use of **DSA concepts** with a responsive **Java Swing GUI**.  
It simulates real-world parking operations and can be expanded into a full-scale parking management system.

# How to Run
1. Clone the repository  
2. Open project in any Java IDE (IntelliJ / NetBeans / Eclipse)  
3. Run the main Java file  
4. Enjoy the Smart Parking System 
