sudo apt-get update

#install Java, Maven, and Tomcat
sudo apt-get install -y openjdk-7-jdk
sudo apt-get install -y maven
sudo apt-get install -y Tomcat

# install git
sudo apt-get install -y git

git clone https://github.com/SoftwareEngineeringToolDemos/ICSE-2012-DSOL.git /home/vagrant/ICSE-2012-DSOL
chmod +x /home/vagrant/ICSE-2012-DSOL
mv /home/vagrant/ICSE-2012-DSOL/build-vm/vm-contents/* /home/vagrant/Desktop/

#set up sidebar
sudo rm -f "/usr/share/applications/libreoffice-writer.desktop" 2 > /dev/null
sudo rm -f "/usr/share/applications/libreoffice-calc.desktop" 2 > /dev/null
sudo rm -f "/usr/share/applications/libreoffice-impress.desktop" 2 > /dev/null
sudo rm -f "/usr/share/applications/amazon-default.desktop" 2 > /dev/null
sudo rm -f "/usr/share/applications/ubuntu-software-center.desktop" 2 > /dev/null

# build DSOL
git clone https://github.com/leandroshp/jAMPL.git /home/vagrant/jAMPL
cd /home/vagrant/jAMPL
mvn clean
mvn -D maven.test.skip=true install
cd /home/vagrant/ICSE-2012-DSOL
mvn clean
mvn install

# launch DSOL
cd /home/vagrant/ICSE-2012-DSOL/poll-translator
mvn clean compile exec:java &

#wait for DSOL build to complete before opening web app
sleep 10

# open DSOL web app
xdg-open http://localhost:8088/poll-translator/api/app