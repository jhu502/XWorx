package plm.part.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import plm.part.XPartUsageLink;
import com.flame.orm.PersistenceHelper;

@Service
public class XPartStandardService {
	public List<?> getUsesOccurrences(Collection<XPartUsageLink> links) {
		List<Long> list = new ArrayList<>();
		for (XPartUsageLink link : links) {
			list.add(link.getXid());
		}
		return PersistenceHelper.service().query("select a from XPartUsageLink a where a.usageLink.usage_id in :ids", new Object[][] { { "ids", list.toArray(new Long[0]) } });
	}
}
