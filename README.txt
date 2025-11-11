Drop CSV files into the "/home/mat/Code/cgt-coinbase/data" folder.

Then run LoadData.  This will use the unique id within each CSV line to find rows that it has not seen
before and then add the new ones to the databse table that is stored in MySQL.

Once that is done, the CalculateTotlas function will generate the report.  The report location is defined at the 
top of the file, and is currently set to "/home/shares/tmp" for ease of access.

This tools does not deal with tax years, but would need the following changes:
* Define the tax year boundries.
* Define an "opening balance section" of the report
* Process all of the transactions within a year.
* The "Closing Balances" section of one year is the "Opening Balances" section of the following year.

!!!! Right now you need to change the TableRow.java file line 45 were there is a hard-coded date. !!!!
