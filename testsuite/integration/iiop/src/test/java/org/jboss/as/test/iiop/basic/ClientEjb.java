package org.jboss.as.test.iiop.basic;

import java.rmi.RemoteException;

import javax.ejb.EJB;
import javax.ejb.EJBMetaData;
import javax.ejb.Handle;
import javax.ejb.HomeHandle;
import javax.ejb.Stateless;
import javax.rmi.PortableRemoteObject;

import junit.framework.Assert;
import org.jboss.ejb.iiop.HandleImplIIOP;

/**
 * @author Stuart Douglas
 */
@Stateless
public class ClientEjb {

    @EJB(lookup = "corbaname:iiop:localhost:3628#IIOPBasicBean")
    private IIOPBasicHome home;


    @EJB(lookup = "corbaname:iiop:localhost:3628#IIOPBasicStatefulBean")
    private IIOPBasicStatefulHome statefulHome;

    public String getRemoteMessage() throws RemoteException {
        return home.create().hello();
    }

    public String getRemoteViaHomeHandleMessage() throws RemoteException {
        final HomeHandle handle = home.getHomeHandle();
        final IIOPBasicHome newHome = (IIOPBasicHome) PortableRemoteObject.narrow(handle.getEJBHome(), IIOPBasicHome.class);
        final IIOPBasicRemote object = newHome.create();
        return object.hello();
    }


    public String getRemoteViaHandleMessage() throws RemoteException {

        final IIOPBasicRemote object = home.create();
        final Handle handle = object.getHandle();
        final IIOPBasicRemote newObject = (IIOPBasicRemote) PortableRemoteObject.narrow(handle.getEJBObject(), IIOPBasicRemote.class);
        return newObject.hello();
    }

    public String getRemoteViaWrappedHandle() throws RemoteException {

        final IIOPBasicRemote object = home.create();
        final Handle handle = object.wrappedHandle().getHandle();
        Assert.assertEquals(HandleImplIIOP.class, handle.getClass());
        final IIOPBasicRemote newObject = (IIOPBasicRemote) PortableRemoteObject.narrow(handle.getEJBObject(), IIOPBasicRemote.class);
        return newObject.hello();
    }

    public String getRemoteMessageViaEjbMetadata() throws RemoteException {
        final EJBMetaData metadata = home.getEJBMetaData();
        final IIOPBasicHome newHome = (IIOPBasicHome) PortableRemoteObject.narrow(metadata.getEJBHome(), IIOPBasicHome.class);
        final IIOPBasicRemote object = newHome.create();
        Assert.assertEquals(IIOPBasicHome.class, metadata.getHomeInterfaceClass());
        Assert.assertEquals(IIOPBasicRemote.class, metadata.getRemoteInterfaceClass());
        return object.hello();
    }

    public void testIsIdentical() throws RemoteException {
        final IIOPBasicStatefulRemote b1 = statefulHome.create(10);
        final IIOPBasicStatefulRemote b2 = statefulHome.create(20);
        Assert.assertTrue(b1.isIdentical(b1));
        Assert.assertFalse(b1.isIdentical(b2));

        final IIOPBasicRemote s1 = home.create();
        final IIOPBasicRemote s2 = home.create();
        Assert.assertTrue(s1.isIdentical(s1));
        Assert.assertTrue(s1.isIdentical(s2));
    }
}
