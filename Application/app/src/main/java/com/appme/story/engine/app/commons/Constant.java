package com.appme.story.engine.app.commons;

import java.util.ArrayList;
import java.util.Arrays;

public class Constant {
    public static final String BROADCAST_ACTION = "com.appme.story.process.BROADCAST";
    public static final String STATUS_KEY = "com.appme.story.process.STATUS_KEY";
    public static final String STATUS_MESSAGE = "com.appme.story.process.STATUS_MESSAGE";
    public static final int PROCESS_NOTIFICATION_ID = 1;
    public static final String URL = "url";
    public static final String MODEL = "model";
    public static final String TOAST_MSG = "toast_msg";
    public static final String NOTIFICATION_ICON = "notification_icon";
    public static final String IS_SHOW_TOAST_MSG = "is_show_toast";
    public static final String MUST_UPDATE = "must_update";
    public static final String BINARY_SU = "su";
    public static final String BINARY_BUSYBOX = "busybox";
    public static final String ACTION_ACCESSIBILITY_ACTION = "com.appme.story.ACCESSIBILITY_ACTION";
	public static final String EXTRA_ACTION = "action";
	public static final String FILE_PATH = "file_name";     // extras indicators
    public static final String LISTENING_PORT = "listen_port";
    public static final int LISTENING_PORT_DEF = 8888;
    public static final String START_AT_BOOT = "start_at_boot";
    public static final boolean START_AT_BOOT_DEF = false;
    public static final String HTTP_AUTHENTICATION = "http_authentication";
    public static final boolean HTTP_AUTHENTICATION_DEF = false;
    public static final String HTTP_AUTHENTICATION_USER = "http_user";
    public static final String HTTP_AUTHENTICATION_USER_DEF = "myuser";
    public static final String HTTP_AUTHENTICATION_PASSWORD = "http_password";
    public static final String HTTP_AUTHENTICATION_PASSWORD_DEF = "password";
    public static final String APP_LIST = "setAppList";
    public static final int PICK_APP_REQUEST = 1;
    
    public Constant() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation");
    }
	
    public interface ACTION {
        String START_SERVICE = "com.appme.story.process.action.START_SERVICE";
        String CHECK_INDEX_FILE = "com.appme.story.process.action.CHECK_INDEX_FILE";
        String EXTRACT_ASSETS = "com.appme.story.process.action.EXTRACT_ASSETS";
        String INSTALL_INDEX_FILE = "com.appme.story.process.action.INSTALL_INDEX_FILE";
        String SYNCRON_DATA = "com.appme.story.process.action.SYNCRON_DATA"; 
        String START_ACTIVITY = "com.appme.story.process.action.START_ACTIVITY";  
        String STOP_SERVICE = "com.appme.story.process.action.STOP_SERVICE";
    }
	
	public static final String[] knownRootAppsPackages = {
		"com.noshufou.android.su",
		"com.noshufou.android.su.elite",
		"eu.chainfire.supersu",
		"com.koushikdutta.superuser",
		"com.thirdparty.superuser",
		"com.yellowes.su",
		"com.topjohnwu.magisk",
		"com.kingroot.kinguser",
		"com.kingo.root",
		"com.smedialink.oneclickroot",
		"com.zhiqupk.root.global",
		"com.alephzain.framaroot"
    };

    public static final String[] knownDangerousAppsPackages = {
		"com.koushikdutta.rommanager",
		"com.koushikdutta.rommanager.license",
		"com.dimonvideo.luckypatcher",
		"com.chelpus.lackypatch",
		"com.ramdroid.appquarantine",
		"com.ramdroid.appquarantinepro",
		"com.android.vending.billing.InAppBillingService.COIN",
		"com.android.vending.billing.InAppBillingService.LUCK",
		"com.chelpus.luckypatcher",
		"com.blackmartalpha",
		"org.blackmart.market",
		"com.allinone.free",
		"com.repodroid.app",
		"org.creeplays.hack",
		"com.baseappfull.fwd",
		"com.zmapp",
		"com.dv.marketmod.installer",
		"org.mobilism.android",
		"com.android.wp.net.log",
		"com.android.camera.update",
		"cc.madkite.freedom",
		"com.solohsu.android.edxp.manager",
		"org.meowcat.edxposed.manager",
		"com.xmodgame",
		"com.cih.game_cih",
		"com.charles.lpoqasert",
		"catch_.me_.if_.you_.can_"
    };

    public static final String[] knownRootCloakingPackages = {
		"com.devadvance.rootcloak",
		"com.devadvance.rootcloakplus",
		"de.robv.android.xposed.installer",
		"com.saurik.substrate",
		"com.zachspong.temprootremovejb",
		"com.amphoras.hidemyroot",
		"com.amphoras.hidemyrootadfree",
		"com.formyhm.hiderootPremium",
		"com.formyhm.hideroot"
    };

    // These must end with a /
    private static final String[] suPaths = {
		"/data/local/",
		"/data/local/bin/",
		"/data/local/xbin/",
		"/sbin/",
		"/su/bin/",
		"/system/bin/",
		"/system/bin/.ext/",
		"/system/bin/failsafe/",
		"/system/sd/xbin/",
		"/system/usr/we-need-root/",
		"/system/xbin/",
		"/cache/",
		"/data/",
		"/dev/"
    };


    public static final String[] pathsThatShouldNotBeWritable = {
		"/system",
		"/system/bin",
		"/system/sbin",
		"/system/xbin",
		"/vendor/bin",
		"/sbin",
		"/etc",
		//"/sys",
		//"/proc",
		//"/dev"
    };

    /**
     * Get a list of paths to check for binaries
     *
     * @return List of paths to check, using a combination of a static list and those paths
     * listed in the PATH environment variable.
     */
    public static String[] getPaths() {
        ArrayList<String> paths = new ArrayList<>(Arrays.asList(suPaths));

        String sysPaths = System.getenv("PATH");

        // If we can't get the path variable just return the static paths
        if (sysPaths == null || "".equals(sysPaths)) {
            return paths.toArray(new String[0]);
        }

        for (String path : sysPaths.split(":")) {

            if (!path.endsWith("/")) {
                path = path + '/';
            }

            if (!paths.contains(path)) {
                paths.add(path);
            }
        }

        return paths.toArray(new String[0]);
    }
}
