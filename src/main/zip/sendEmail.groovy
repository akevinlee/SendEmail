import com.urbancode.air.AirPluginTool

import javax.mail.internet.*;
import javax.mail.*

def apTool = new AirPluginTool(this.args[0], this.args[1]);
def props = apTool.getStepProperties();

def mailServer = props['mailServer'];
def mailPort = props['mailPort'];
def mailFrom = props['mailFrom'];
def mailAuthenticate = props['mailAuthenticate'];
def mailUsername = props['mailUsername'];
def mailPassword = props['mailPassword'];
def sendAsHTML = props['sendAsHTML'];
def mailUseTLS = props['mailUseTLS'];
def toList = props['toList'];
def subject = props['subject'];
def body = props['body'];

Properties mProps = System.getProperties();
// mail.smtp.ssl.trust is needed in script to avoid error "Could not convert socket to TLS"
mProps.setProperty("mail.smtp.ssl.trust", mailServer);
mProps.put("mail.smtp.auth", mailAuthenticate);
mProps.put("mail.smtp.host", mailServer);
mProps.put("mail.smtp.user", mailUsername);
mProps.put("mail.smtp.password", mailPassword);
mProps.put("mail.smtp.port", mailPort);
mProps.put("mail.smtp.starttls.enable", mailUseTLS);

Session session = Session.getDefaultInstance(mProps, null);
MimeMessage msg = new MimeMessage(session);

// tokenize out the recipients in case they came in as a list
StringTokenizer tok = new StringTokenizer(toList.toString(), ",");
ArrayList emailTos = new ArrayList();
while (tok.hasMoreElements()){
    emailTos.add(new InternetAddress(tok.nextElement().toString()));
}

content = """\
<html>
<body>
${body}
</body>
</html>
"""

InternetAddress[] to = new InternetAddress[emailTos.size()];
to = (InternetAddress[]) emailTos.toArray(to);
msg.setRecipients(MimeMessage.RecipientType.TO,to);
msg.setFrom(new InternetAddress(mailFrom.toString()));
msg.setSubject(subject.toString());
if (sendAsHTML == "true") {
    println "constructing HTML mail message";
	msg.setContent(content.toString(), "text/html");
} else {
    println "constructing plain text message";
	msg.setText(body);  
}

try {
    Transport transport = session.getTransport("smtp");
    transport.connect(mailServer.toString(), mailUsername.toString(), mailPassword.toString());
    transport.sendMessage(msg, msg.getAllRecipients());
    transport.close();
    println "mail sent successfully from user ${mailFrom} to: ${toList}";
} catch (MessagingException mex) {
    mex.printStackTrace();
}

System.exit(0);




                          
