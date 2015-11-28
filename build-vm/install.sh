sudo apt-get update

#install Java and Maven
sudo apt-get install -y openjdk-7-jdk
sudo apt-get install -y maven

# install git
sudo apt-get install -y git

# call script to install DSOL, move to vagrant file?
#sh ./dsol.sh

git clone https://github.com/SoftwareEngineeringToolDemos/ICSE-2012-DSOL.git /home/vagrant/ICSE-2012-DSOL
chmod +x /home/vagrant/ICSE-2012-DSOL
mv /home/vagrant/ICSE-2012-DSOL/build-vm/vm-contents/* /home/vagrant/Desktop/

#set up sidebar
gsettings reset com.canonical.Unity.Launcher favorites
gsettings set com.canonical.Unity.Launcher favorites "['application://nautilus.desktop', 'application://firefox.desktop', 'application://gnome-terminal.desktop', 'application://unity-control-center.desktop', 'unity://running-apps', 'unity://expo-icon', 'unity://devices']"

#set up sidebar
sudo rm -f /user/share/applications/libreoffice-writer.desktop
sudo rm -f /user/share/applications/libreoffice-calc.desktop
sudo rm -f /user/share/applications/libreoffice-impress.desktop
sudo rm -f /user/share/applications/amazon-default.desktop
sudo rm -f /user/share/applications/ubuntu-software-center.desktop

# Reboot the machine
#sudo reboot

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
#xdg-open http://localhost:8088/poll-translator/api/app