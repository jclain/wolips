/* WOSrcTask.java
 * Created on 12 janv. 2009
 */
package run.univ.wosrc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.objectstyle.wolips.builder.BuilderPlugin;
import org.objectstyle.wolips.builder.internal.BuildHelper;
import org.objectstyle.wolips.builder.internal.ResourceUtilities;
import org.objectstyle.wolips.builder.internal.BuildHelper.BuildtaskAbstract;
import org.xml.sax.InputSource;

import run.univ.Str;

public class WOSrcTask extends BuildtaskAbstract {
    public static IWorkspaceRoot getWorkspaceRoot() {
        return ResourcesPlugin.getWorkspace().getRoot();
    }

    public WOSrcTask(IResource res, IPath destination, String msgPrefix) {
        _res = res;
        _dest = destination;
        _msgPrefix = msgPrefix;
        _workAmount = 1000;
    }

    private static final Writer openOutput(OutputStream os) throws IOException {
        if (os == null) return null;
        return new OutputStreamWriter(os, Str.UTF_8);
    }

    private static final ByteArrayInputStream openBuffer(ByteArrayOutputStream buffer)
            throws IOException {
        return new ByteArrayInputStream(buffer.toByteArray());
    }

    public void doWork(IProgressMonitor m) throws CoreException {
        IPath base = _dest.removeFileExtension();
        String name = base.lastSegment();
        base = base.addFileExtension("wo");

        String error = null;
        try {
            int n = base.segmentCount() - 3;
            IPath dstShortened;
            if (n > 0) dstShortened = base.removeFirstSegments(n);
            else dstShortened = base;
            m.subTask("create " + dstShortened);

            IFile file = (IFile)_res;
            InputSource input = new InputSource(file.getFullPath().toString());
            InputStreamReader r = new InputStreamReader(file.getContents(), file.getCharset());
            input.setCharacterStream(r);

            ByteArrayOutputStream htmlBuffer = new ByteArrayOutputStream();
            ByteArrayOutputStream wodBuffer = new ByteArrayOutputStream();
            ByteArrayOutputStream wooBuffer = new ByteArrayOutputStream();

            WOSrcGenerator wsg = new WOSrcGenerator(input, openOutput(htmlBuffer),
                    openOutput(wodBuffer), openOutput(wooBuffer), Str.UTF_8, true);
            wsg.generate(true, true);

            ResourceUtilities.checkDir(base, m);

            IPath html = base.append(name + ".html");
            ResourceUtilities.checkDestination(html, m, true);
            IFile htmlF = getWorkspaceRoot().getFile(html);
            htmlF.create(openBuffer(htmlBuffer), true, m);
            htmlF.setDerived(true, m);

            IPath wod = base.append(name + ".wod");
            ResourceUtilities.checkDestination(wod, m, true);
            IFile wodF = getWorkspaceRoot().getFile(wod);
            wodF.create(openBuffer(wodBuffer), true, m);
            wodF.setDerived(true, m);

            IPath woo = base.append(name + ".woo");
            ResourceUtilities.checkDestination(woo, m, true);
            IFile wooF = getWorkspaceRoot().getFile(woo);
            wooF.create(openBuffer(wooBuffer), true, m);
            wooF.setDerived(true, m);
        } catch (CoreException up) {
            error = " *failed*: " + _res.getProjectRelativePath().toString() + " ("
                    + up.getMessage() + ")";
            _getLogger().debug(_msgPrefix + error, up);
            // up.printStackTrace();
            // m.setCanceled(true);
            // throw up;
        } catch (Exception up) {
            error = " *failed*: " + _res.getProjectRelativePath().toString() + " ("
                    + up.getMessage() + ")";
            _getLogger().log(_msgPrefix + error, up);
            // up.printStackTrace();
            // throw up;
        }
        if (null == error) {
            // _res.deleteMarkers(IMarker.PROBLEM, true, 1);
            _res.deleteMarkers(BuilderPlugin.MARKER_BUILD_PROBLEM, true, 0);
        } else {
            BuildHelper.markResource(
                    _res,
                    BuilderPlugin.MARKER_BUILD_PROBLEM,
                    IMarker.SEVERITY_ERROR,
                    error,
                    _dest.toString());
        }
    }

    IResource _res;

    IPath _dest;

    String _msgPrefix;
}