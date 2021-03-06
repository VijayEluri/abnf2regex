package net.abnf2regex;

import java.io.EOFException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.HashSet;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test {@link WildcardFragment}.
 */
public class WildcardFragmentTest
{

    /**
     * Test method for
     * {@link net.abnf2regex.WildcardFragment#setOccurences(net.abnf2regex.OccurrenceRange)}
     * .
     */
    @Test
    public void testSetOccurences()
    {
        WildcardFragment wc = new WildcardFragment("text"); //$NON-NLS-1$
        wc.setOccurences(new OccurrenceRange(1, -1)); // should do nothing
        Assert.assertEquals(OccurrenceRange.ONCE, wc.getOccurences());
    }

    /**
     * Test method for {@link net.abnf2regex.WildcardFragment#needsAbnfParens()}
     * .
     */
    @Test
    public void testNeedsAbnfParens()
    {
        Assert.assertFalse(new WildcardFragment("message").needsAbnfParens()); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link net.abnf2regex.WildcardFragment#buildAbnf(java.lang.StringBuilder)}
     * .
     */
    @Test
    public void testBuildAbnf()
    {
        String message = "string"; //$NON-NLS-1$
        WildcardFragment wc = new WildcardFragment(message);
        StringBuilder bld = new StringBuilder();
        Assert.assertSame(bld, wc.buildAbnf(bld, new HashSet<String>()));
        Assert.assertEquals('<' + message + '>', bld.toString());
    }

    /**
     * Test method for
     * {@link net.abnf2regex.WildcardFragment#needsRegexParens()}.
     */
    @Test
    public void testNeedsRegexParens()
    {
        Assert.assertFalse(new WildcardFragment("thing").needsRegexParens()); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link net.abnf2regex.WildcardFragment#buildRegex(java.io.PrintWriter, java.util.Set)}
     * .
     */
    @Test
    public void testBuildRegex()
    {
        PrintWriter mockPw = EasyMock.createMock(PrintWriter.class);
        mockPw.print(".*"); //$NON-NLS-1$
        EasyMock.replay(mockPw);

        WildcardFragment wc = new WildcardFragment();
        HashSet<String> set = new HashSet<String>();
        wc.buildRegex(mockPw, set);
        Assert.assertEquals(0, set.size());
        EasyMock.verify(mockPw);
    }

    /**
     * Test method for
     * {@link net.abnf2regex.WildcardFragment#append(net.abnf2regex.RuleFragment)}
     * .
     */
    @Test
    public void testAppend()
    {
        Assert.assertFalse(new WildcardFragment().append(null));
    }

    /**
     * Test method for {@link net.abnf2regex.WildcardFragment#clone()}.
     */
    @Test
    public void testClone()
    {
        WildcardFragment wc = new WildcardFragment("cloning"); //$NON-NLS-1$
        Object clone = wc.clone();
        Assert.assertTrue(clone instanceof WildcardFragment);
        Assert.assertEquals(clone.toString(), wc.toString()); // abnf being
                                                              // equal is enough
    }

    /**
     * Test method for
     * {@link net.abnf2regex.WildcardFragment#parse(net.abnf2regex.AbnfReader)}.
     */
    @Test
    public void testParse()
    {
        try
        {
            String message = "parsed"; //$NON-NLS-1$
            Reader mockReader = EasyMock.createMock(Reader.class);
            AbnfReader abnf = new AbnfReader(mockReader, "mock"); //$NON-NLS-1$
            EasyMock.expect(Integer.valueOf(abnf.read())).andReturn(Integer.valueOf('<'));
            for (int i = 0; i < message.length(); ++i)
            {
                EasyMock.expect(Integer.valueOf(abnf.read())).andReturn(Integer.valueOf(message.charAt(i)));
            }
            EasyMock.expect(Integer.valueOf(abnf.read())).andReturn(Integer.valueOf('>'));
            EasyMock.replay(mockReader);
            WildcardFragment wc = WildcardFragment.parse(abnf);
            Assert.assertEquals('<' + message + '>', wc.toString());
            EasyMock.verify(mockReader);
        }
        catch (IOException ex)
        {
            Assert.fail(ex.toString());
        }
    }

    /**
     * Test method for
     * {@link net.abnf2regex.WildcardFragment#parse(net.abnf2regex.AbnfReader)}
     * with exception.
     */
    @Test
    public void testParseEx()
    {
        String message = "cutoff"; //$NON-NLS-1$
        AbnfReader abnf = EasyMock.createMock(AbnfReader.class);
        try
        {
            EasyMock.expect(Integer.valueOf(abnf.read())).andThrow(new EOFException());
            EasyMock.replay(abnf);

            WildcardFragment wc = WildcardFragment.parse(abnf);
            Assert.assertEquals('<' + message + '>', wc.toString());
            Assert.fail("expected EOFException"); //$NON-NLS-1$
        }
        catch (EOFException eofex)
        {
            // NOP, expected
        }
        catch (IOException ioex)
        {
            Assert.fail("unexpected exception: " + ioex.toString()); //$NON-NLS-1$
        }
        EasyMock.verify(abnf);
    }
}
