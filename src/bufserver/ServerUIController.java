package bufserver;

import AllProv.Comments;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import java.net.URL;
import javafx.fxml.Initializable;
import UserRegis.InsertUser;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import CheckExistence.CheckPerson;
import AllUsers.DisplayUsers;
import ProRegis.InsertProvider;
import AllProv.DisplayProviders;
import AllProv.InsertFoodItem;
import AllProv.MyCustomers;
import AllProv.RemoveFood;
import AllProv.RemoveFoodCommand;
import AllProv.UpdataPass;
import AllUsers.AfterHandler;
import AllUsers.AfterHandlerForTop;
import AllUsers.BuyOperation;
import DeleteProvider.deleteProvider;
import AllUsers.userPassUpdate;
import AllUsers.RemoveMyAccount;
import AllUsers.ChangeAccType;
import AllUsers.MyRecord;
import AllUsers.CustMessages;
import AllUsers.SearchByFoodN;
import AllUsers.SearchByTop;
import Services.AddAdvert;
import Services.PDFReport;
import Services.RandomAdChooser;

public class ServerUIController implements Initializable {

    @FXML
    private AnchorPane serverBack;
    @FXML
    private ImageView serverImage;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        rotator();
        
        PDFReport pdf=new PDFReport();
        Thread pdfr=new Thread(pdf);
        pdfr.start();

        RandomAdChooser ra=new RandomAdChooser();
        Thread ract=new Thread(ra);
        ract.start();
        
        AddAdvert aa=new AddAdvert();
        Thread aat=new Thread(aa);
        aat.start();
        
        Comments c=new Comments();
        Thread ct=new Thread(c);
        ct.start();
        
        MyCustomers mcs=new MyCustomers();
        Thread mcst=new Thread(mcs);
        mcst.start();
        
        SearchByTop sbt=new SearchByTop();
        Thread sbtt=new Thread(sbt);
        sbtt.start();
        
        AfterHandlerForTop ahft=new AfterHandlerForTop();
        Thread afftt=new Thread(ahft);
        afftt.start();
        
        AfterHandler ah=new AfterHandler();
        Thread aft=new Thread(ah);
        aft.start();
        
        BuyOperation bo=new BuyOperation();
        Thread bot=new Thread(bo);
        bot.start();
        
        SearchByFoodN sbfn=new SearchByFoodN();
        Thread sbfnt=new Thread(sbfn);
        sbfnt.start();
        
        CustMessages cmess=new CustMessages();
        Thread cmesst=new Thread(cmess);
        cmesst.start();
        
        MyRecord mr=new MyRecord();
        Thread mrt=new Thread(mr);
        mrt.start();
        
        ChangeAccType cat=new ChangeAccType();
        Thread catt=new Thread(cat);
        catt.start();
        
        RemoveMyAccount rma=new RemoveMyAccount();
        Thread rmat=new Thread(rma);
        rmat.start();
        
        userPassUpdate upu=new userPassUpdate();
        Thread uput=new Thread(upu);
        uput.start();
        
        RemoveFoodCommand rfc=new RemoveFoodCommand();
        Thread tt=new Thread(rfc);
        tt.start();
        
        RemoveFood rf=new RemoveFood();
        Thread rft=new Thread(rf);
        rft.start();
        
        UpdataPass up=new UpdataPass();
        Thread updatr=new Thread(up);
        updatr.start();
        
        
        InsertUser userIns= new InsertUser();
        Thread inser= new Thread(userIns);
        inser.start();
        
        CheckPerson che=new CheckPerson();
        Thread checker=new Thread(che);
        checker.start();
        
        DisplayUsers du=new DisplayUsers();
        Thread viewU= new Thread(du);
        viewU.start();
        
        InsertProvider proIns= new InsertProvider();
        Thread in= new Thread(proIns);
        in.start();
        
        DisplayProviders showP=new DisplayProviders();
        Thread disP=new Thread(showP);
        disP.start();
        
        deleteProvider delp=new deleteProvider();
        Thread del=new Thread(delp);
        del.start();
        
        
        InsertFoodItem ifi=new InsertFoodItem();
        Thread ifitr=new Thread(ifi);
        ifitr.start();
        
    }

    public void rotator(){
        RotateTransition rt = new RotateTransition(Duration.millis(3000), serverImage);
        rt.setByAngle(360);
        rt.setCycleCount(Animation.INDEFINITE);
        rt.setInterpolator(Interpolator.LINEAR);
        rt.play();
    }    

    @FXML
    private void close(MouseEvent event) {
        System.exit(0);
    }
    
}
