package cn.sky.tools.idgetter.facade;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import cn.sky.tools.idgetter.ISequenceRepository;
import cn.sky.tools.idgetter.entity.IdGetter;
import lombok.Setter;

public class IdGetterFactory {

    private static final ExecutorService THREAD_POOL = new ThreadPoolExecutor(5, 20,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(16), new ThreadFactoryBuilder()
            .setNameFormat("ID-GETTER-Execute-Pool-%d").build(), new ThreadPoolExecutor.CallerRunsPolicy());

    private static final ConcurrentHashMap<String, IdGetter> bizTagIdLeaf = new ConcurrentHashMap<>();

    @Resource
    @Setter
    private ISequenceRepository sequenceRepository;

    public Long getNextId(String bizTag) {
        if (bizTagIdLeaf.get(bizTag) == null) {
            synchronized (bizTagIdLeaf) {
                if (bizTagIdLeaf.get(bizTag) == null) {
                    IdGetter idGetter = new IdGetter();
                    idGetter.setBizTag(bizTag);
                    idGetter.setAsyncLoadingSegment(true);
                    idGetter.setTaskExecutor(THREAD_POOL);
                    idGetter.setSequenceRepository(sequenceRepository);
                    idGetter.setIncrSize(5000L);
                    idGetter.init();
                    bizTagIdLeaf.putIfAbsent(bizTag, idGetter);
                }
            }
        }
        return bizTagIdLeaf.get(bizTag).getId();
    }

}
