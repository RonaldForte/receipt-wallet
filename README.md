# 📘 Receipt Tracker

A simple, fast, and friendly desktop app for managing your receipts.

Receipt Tracker lets you easily store, view, and export your receipts all in a clean JavaFX interface.
Built with Java, JavaFX, H2 database, and SOLID architecture, this app is designed to be plug-and-play: just clone and run.

Perfect for anyone who wants a lightweight, local, no-setup-required receipt organizer.

## ✨ Features
✔ Add & Edit Receipts

- Store name

- Amount

- Date of purchase

✔ Dashboard Overview

The dashboard provides quick insights such as:

- Monthly spending bar chart

- Store-wise spending pie chart

- Total receipts and totals at a glance

✔ Export Your Data

Export your receipts into clean, portable formats:

- CSV

- PDF

Your export files appear instantly where you choose.

✔ Local Database (No Setup Needed)

Uses H2 embedded database, meaning:

- No installation required

- Works on any machine

- Database lives inside the app directory

- Clean and self-contained

## 🚀 Download
Choose the JAR for your OS from the Releases page:

- macOS: ReceiptTracker-mac-1.0.0.jar  
- Windows: ReceiptTracker-win-1.0.0.jar  
- Linux: Not Supported Yet!

## 🚀 How to Run
There are 2 ways!

1. Clone the repository:
  `git clone https://github.com/RonaldForte/receipt-wallet.git`

2. Open the project in your IDE of choice

3. Run the following Gradle command:
  `./gradlew run`

The app will launch immediately!

OR

1. Go to the Releases page and download the latest receipt-tracker-all.jar file for your os.

2. Make sure you have Java installed. Receipt Tracker requires Java 24 or newer. You can check your version by running the following command:
  `java -version`

3. Open a terminal/command prompt, navigate to the folder where the JAR is, and run:
  `java -jar ReceiptTracker-1.0.0.jar`

The app should launch immediately!
I would like to highlight that this app runs with an embedded H2 database so no need for any configuration there.

## MIT License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details