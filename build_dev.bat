call mvn clean install -P prod -P dev
call mvn exec:java -Dexec.mainClass="sh.app.ticket_printer.PrinterApplet"
