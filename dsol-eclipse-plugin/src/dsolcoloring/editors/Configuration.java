package dsolcoloring.editors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

class Configuration extends SourceViewerConfiguration {
      public IPresentationReconciler getPresentationReconciler(
         ISourceViewer sourceViewer) {
         PresentationReconciler pr = new PresentationReconciler();
         DefaultDamagerRepairer ddr = new DefaultDamagerRepairer(new DSOLScanner());
         pr.setRepairer(ddr, IDocument.DEFAULT_CONTENT_TYPE);
         pr.setDamager(ddr, IDocument.DEFAULT_CONTENT_TYPE);
         return pr;
      }
   }